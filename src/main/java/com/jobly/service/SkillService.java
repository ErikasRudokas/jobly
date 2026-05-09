package com.jobly.service;

import com.jobly.dao.SkillDao;
import com.jobly.exception.general.NotFoundException;
import com.jobly.gen.api.parser.data.EmbeddingRequest;
import com.jobly.gen.api.parser.data.EmbeddingResponse;
import com.jobly.gen.model.*;
import com.jobly.mapper.SkillMapper;
import com.jobly.model.SkillAliasEntity;
import com.jobly.model.SkillEntity;
import com.jobly.service.api.CvParserApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillService {

    private final SkillDao skillDao;
    private final CvParserApiService cvParserApiService;

    public SearchSkillsResponse searchSkills(String value, SkillType skillType, Integer offset, Integer limit) {
        SearchSkillsResponse response = new SearchSkillsResponse();
        var skillEntities = skillDao.searchSkills(value, skillType, offset, limit);
        Integer totalSkillCount = skillDao.countSkillsBySearch(value, skillType);
        response.setSkills(getSkillDetailsList(skillEntities));
        response.setTotal(totalSkillCount);
        return response;
    }

    private static List<SkillDetails> getSkillDetailsList(List<SkillEntity> skills) {
        return skills.stream()
                .map(SkillMapper::toSkillDetails)
                .toList();
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public Skill createSkill(SkillCreateRequest skillCreateRequest) {
        Float[] embedding = embedDescription(skillCreateRequest.getDescription());

        SkillEntity skillEntity = SkillMapper.toSkillEntity(skillCreateRequest, embedding);

        SkillEntity savedSkill = skillDao.save(skillEntity);
        List<SkillAliasEntity> savedAliases = persistSkillAliases(savedSkill, skillCreateRequest.getAliases());
        return SkillMapper.toSkill(savedSkill, savedAliases);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public Skill getSkillById(Long id) {
        SkillEntity skillEntity = skillDao.findById(id);
        List<SkillAliasEntity> aliases = skillDao.findAliasesBySkillId(id);
        return SkillMapper.toSkill(skillEntity, aliases);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public Skill updateSkill(Long id, SkillUpdateRequest skillUpdateRequest) {
        SkillEntity skillEntity = skillDao.findById(id);

        boolean descriptionChanged = false;
        if (skillUpdateRequest.getDescription() != null) {
            descriptionChanged = !Objects.equals(skillEntity.getDescription(), skillUpdateRequest.getDescription());
            skillEntity.setDescription(skillUpdateRequest.getDescription());
        }

        SkillMapper.updateEntity(skillEntity, skillUpdateRequest);

        if (descriptionChanged) {
            skillEntity.setEmbedding(embedDescription(skillEntity.getDescription()));
        }

        SkillEntity savedSkill = skillDao.save(skillEntity);
        List<SkillAliasEntity> updatedAliases = processSkillAliases(savedSkill, skillUpdateRequest.getAliases());
        return SkillMapper.toSkill(savedSkill, updatedAliases);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public void deleteSkillById(Long id) {
        skillDao.deleteById(id);
    }

    private Float[] embedDescription(String description) {
        EmbeddingRequest embeddingRequest = new EmbeddingRequest().description(description);
        EmbeddingResponse embeddingResponse = cvParserApiService.embed(embeddingRequest);
        return Optional.ofNullable(embeddingResponse)
                .map(EmbeddingResponse::getEmbedding)
                .map(list -> list.toArray(new Float[0]))
                .orElseGet(() -> new Float[0]);
    }

    private List<SkillAliasEntity> persistSkillAliases(SkillEntity skillEntity, List<SkillAliasCreateRequest> aliases) {
        if (ObjectUtils.isEmpty(aliases)) {
            SkillAliasCreateRequest defaultAlias = new SkillAliasCreateRequest();
            defaultAlias.setValue(skillEntity.getName());
            aliases = List.of(defaultAlias);
        }
        List<SkillAliasEntity> aliasEntities = aliases.stream()
                .map(alias -> SkillMapper.toAliasEntity(skillEntity, alias))
                .toList();

        return skillDao.saveAliases(aliasEntities);
    }

    private List<SkillAliasEntity> processSkillAliases(SkillEntity skillEntity, List<SkillAliasUpdateRequest> aliases) {
        if (aliases == null) {
            return skillDao.findAliasesBySkillId(skillEntity.getId());
        }

        Map<Long, SkillAliasEntity> existingAliases = convertAliasListToMap(skillDao.findAliasesBySkillId(skillEntity.getId()));

        deleteSkillAliases(aliases, existingAliases);
        return persistSkillAliases(aliases, skillEntity, existingAliases);
    }

    private static Map<Long, SkillAliasEntity> convertAliasListToMap(List<SkillAliasEntity> existingAliases) {
        return existingAliases.stream()
                .collect(Collectors.toMap(SkillAliasEntity::getId, alias -> alias));
    }

    private void deleteSkillAliases(List<SkillAliasUpdateRequest> aliases,
                                    Map<Long, SkillAliasEntity> existingAliases) {
        List<SkillAliasEntity> aliasesToDelete = aliases.stream()
                .filter(alias -> Boolean.TRUE.equals(alias.getDelete()))
                .map(alias -> existingAliases.get(alias.getId()))
                .filter(Objects::nonNull)
                .toList();

        skillDao.deleteAliases(aliasesToDelete);
    }

    private List<SkillAliasEntity> persistSkillAliases(List<SkillAliasUpdateRequest> aliases,
                                                       SkillEntity skillEntity,
                                                       Map<Long, SkillAliasEntity> existingAliases) {
        List<SkillAliasEntity> aliasesToSave = aliases.stream()
                .filter(alias -> !Boolean.TRUE.equals(alias.getDelete()))
                .map(alias -> {
                    if (alias.getId() == null) {
                        return SkillMapper.toAliasEntity(skillEntity, alias);
                    }
                    SkillAliasEntity existingAlias = getExistingAlias(existingAliases, alias);
                    SkillMapper.updateAliasEntity(existingAlias, alias);
                    return existingAlias;
                })
                .toList();

        if (aliasesToSave.isEmpty()) {
            SkillAliasEntity defaultAlias = new SkillAliasEntity();
            defaultAlias.setSkill(skillEntity);
            defaultAlias.setAlias(skillEntity.getName().toLowerCase());
            aliasesToSave = List.of(defaultAlias);
        }

        return skillDao.saveAliases(aliasesToSave);
    }

    private static SkillAliasEntity getExistingAlias(Map<Long, SkillAliasEntity> existingAliases, SkillAliasUpdateRequest alias) {
        return Optional.ofNullable(existingAliases.get(alias.getId()))
                .orElseThrow(() -> new NotFoundException("Skill alias not found with id: " + alias.getId()));
    }
}
