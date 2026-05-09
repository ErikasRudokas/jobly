package com.jobly.handler;

import com.jobly.gen.api.SkillsApiDelegate;
import com.jobly.gen.model.*;
import com.jobly.service.SkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SkillApiHandler implements SkillsApiDelegate {

    private final SkillService skillService;

    @Override
    public ResponseEntity<SearchSkillsResponse> searchSkills(String value, SkillType skillType, Integer offset, Integer limit) {
        log.info("Performing a search for skills with value: {}", value);
        return ResponseEntity.ok(skillService.searchSkills(value, skillType, offset, limit));
    }

    @Override
    public ResponseEntity<Skill> createSkill(SkillCreateRequest skillCreateRequest) {
        log.info("Creating a new skill with name: {}", skillCreateRequest.getName());
        return ResponseEntity.ok(skillService.createSkill(skillCreateRequest));
    }

    @Override
    public ResponseEntity<Skill> getSkillById(Long id)  {
        log.info("Getting skill with id: {}", id);
        return ResponseEntity.ok(skillService.getSkillById(id));
    }

    @Override
    public ResponseEntity<Skill> updateSkill(Long id, SkillUpdateRequest skillUpdateRequest) {
        log.info("Updating skill with id: {}", id);
        return ResponseEntity.ok(skillService.updateSkill(id, skillUpdateRequest));
    }

    @Override
    public ResponseEntity<Void> deleteSkillById(Long id) {
        log.info("Deleting skill with id: {}", id);
        skillService.deleteSkillById(id);
        return ResponseEntity.noContent().build();
    }
}
