package com.jobly.dao;

import com.jobly.model.JobSkillEntity;
import com.jobly.repository.JobSkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobSkillDao {

    private final JobSkillRepository jobSkillRepository;

    public List<JobSkillEntity> findAllByJobOfferId(Long id) {
         return jobSkillRepository.findByJobOfferId(id);
    }

    public List<JobSkillEntity> saveAll(List<JobSkillEntity> jobSkillEntities) {
        return jobSkillRepository.saveAll(jobSkillEntities);
    }

    public void deleteAllByIds(List<Long> jobSkillsToDelete, Long jobOfferId) {
        log.info("Deleting job skills with skill ids {} for job offer with id {}", jobSkillsToDelete, jobOfferId);
        jobSkillRepository.deleteAllBySkillIdInAndJobOfferId(jobSkillsToDelete, jobOfferId);
    }
}
