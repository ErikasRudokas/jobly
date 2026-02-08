package com.jobly.handler;

import com.jobly.gen.api.SkillsApiDelegate;
import com.jobly.gen.model.SearchSkillsResponse;
import com.jobly.service.SkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SkillHandler implements SkillsApiDelegate {

    private final SkillService skillService;

    @Override
    public ResponseEntity<SearchSkillsResponse> searchSkills(String value, Integer offset, Integer limit) {
        log.info("Performing a search for skills with value: {}", value);
        return ResponseEntity.ok(skillService.searchSkills(value, offset, limit));
    }
}
