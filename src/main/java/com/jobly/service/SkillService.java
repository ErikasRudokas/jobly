package com.jobly.service;

import com.jobly.dao.SkillDao;
import com.jobly.gen.model.SearchSkillsResponse;
import com.jobly.gen.model.SkillDetails;
import com.jobly.mapper.SkillMapper;
import com.jobly.model.SkillEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillService {

    private final SkillDao skillDao;

    public SearchSkillsResponse searchSkills(String value, Integer offset, Integer limit) {
        log.info("Service layer: Searching skills with value: {}", value);
        SearchSkillsResponse response = new SearchSkillsResponse();
        var skillEntities = skillDao.searchSkills(value, offset, limit);
        Integer totalSkillCount = skillDao.countSkillsBySearch(value);
        response.setSkills(getSkillDetailsList(skillEntities));
        response.setTotal(totalSkillCount);
        return response;
    }

    private static List<SkillDetails> getSkillDetailsList(List<SkillEntity> skills) {
        return skills.stream()
                .map(SkillMapper::toSkillDetails)
                .toList();
    }
}
