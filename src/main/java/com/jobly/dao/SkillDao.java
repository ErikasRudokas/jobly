package com.jobly.dao;

import com.jobly.exception.general.NotFoundException;
import com.jobly.gen.model.SkillType;
import com.jobly.model.SkillAliasEntity;
import com.jobly.model.SkillEntity;
import com.jobly.repository.SkillAliasRepository;
import com.jobly.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SkillDao {

    private final SkillRepository skillRepository;
    private final SkillAliasRepository skillAliasRepository;

    public List<SkillEntity> searchSkills(String value, SkillType skillType, Integer offset, Integer limit) {
        int defaultOffset = (offset != null && offset >= 0) ? offset : 0;
        int defaultLimit = (limit != null && limit > 0) ? limit : 10;
        return skillRepository.findAllSkillsByAliasSearch(value, toSkillTypeParam(skillType), defaultLimit, defaultOffset);
    }

    public Integer countSkillsBySearch(String value, SkillType skillType) {
        return skillRepository.countAllSkillsByAliasSearch(value, toSkillTypeParam(skillType));
    }

    public SkillEntity findById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Skill not found with id: " + id));
    }

    public SkillEntity save(SkillEntity skillEntity) {
        return skillRepository.save(skillEntity);
    }

    public void deleteById(Long id) {
        if (!skillRepository.existsById(id)) {
            throw new NotFoundException("Skill not found with id: " + id);
        }
        skillRepository.deleteById(id);
    }

    public List<SkillAliasEntity> findAliasesBySkillId(Long skillId) {
        return skillAliasRepository.findAllBySkillId(skillId);
    }

    public List<SkillAliasEntity> saveAliases(List<SkillAliasEntity> aliases) {
        return skillAliasRepository.saveAll(aliases);
    }

    public void deleteAliases(List<SkillAliasEntity> aliases) {
        skillAliasRepository.deleteAll(aliases);
    }

    public Set<SkillEntity> findAllByIds(List<Long> ids) {
        return new HashSet<>(skillRepository.findAllById(ids));
    }

    public Map<String, SkillAliasEntity> findAllSkillAliases() {
        List<SkillAliasEntity> skillAliases = skillAliasRepository.findAll();
        return skillAliases.stream()
                .collect(
                        Collectors.toMap(
                                SkillAliasEntity::getAlias,
                                Function.identity())
                );
    }

    private static String toSkillTypeParam(SkillType skillType) {
        return skillType == null ? null : skillType.name();
    }
}
