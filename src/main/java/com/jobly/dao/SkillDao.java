package com.jobly.dao;

import com.jobly.model.SkillEntity;
import com.jobly.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class SkillDao {

    private final SkillRepository skillRepository;

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
}
