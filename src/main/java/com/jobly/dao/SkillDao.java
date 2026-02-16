package com.jobly.dao;

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

    public List<SkillEntity> searchSkills(String value, Integer offset, Integer limit) {
        log.info("Searching skills in the database with value: {}", value);
        int defaultOffset = (offset != null && offset >= 0) ? offset : 0;
        int defaultLimit = (limit != null && limit > 0) ? limit : 10;
        return skillRepository.findAllSkillsByAliasSearch(value, defaultLimit, defaultOffset);
    }

    public Integer countSkillsBySearch(String value) {
        log.info("Counting skills in the database with value: {}", value);
        return skillRepository.countAllSkillsByAliasSearch(value);
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
}
