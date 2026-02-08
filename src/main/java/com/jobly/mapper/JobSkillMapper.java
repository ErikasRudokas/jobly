package com.jobly.mapper;

import com.jobly.gen.model.SkillProficiency;
import com.jobly.model.JobOfferEntity;
import com.jobly.model.JobSkillEntity;
import com.jobly.model.SkillEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class JobSkillMapper {

    public static JobSkillEntity toJobSkillEntity(SkillEntity skillEntity, JobOfferEntity jobOfferEntity, SkillProficiency proficiency) {
        JobSkillEntity jobSkillEntity = new JobSkillEntity();
        jobSkillEntity.setSkill(skillEntity);
        jobSkillEntity.setJobOffer(jobOfferEntity);
        jobSkillEntity.setExpectedProficiency(proficiency);
        return jobSkillEntity;
    }
}
