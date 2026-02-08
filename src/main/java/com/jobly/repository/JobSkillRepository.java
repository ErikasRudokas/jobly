package com.jobly.repository;

import com.jobly.model.JobSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSkillRepository extends JpaRepository<JobSkillEntity, Long> {

    List<JobSkillEntity> findByJobOfferId(Long id);

    @Modifying
    @Query(value = """
            DELETE FROM JOB_SKILLS
            WHERE skill_id IN (:skillIds) AND job_offer_id = :jobOfferId
        """,
        nativeQuery = true)
    void deleteAllBySkillIdInAndJobOfferId(List<Long> skillIds, Long jobOfferId);
}
