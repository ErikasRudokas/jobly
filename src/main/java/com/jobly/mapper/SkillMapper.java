package com.jobly.mapper;

import com.jobly.gen.model.JobOfferSkill;
import com.jobly.gen.model.SkillDetails;
import com.jobly.model.JobSkillEntity;
import com.jobly.model.SkillEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class SkillMapper {

    public static SkillDetails toSkillDetails(SkillEntity skillEntity) {
        SkillDetails skillDetails = new SkillDetails();
        skillDetails.setId(skillEntity.getId());
        skillDetails.setName(skillEntity.getName());
        skillDetails.setType(skillEntity.getSkillType());
        return skillDetails;
    }

    public static JobOfferSkill toJobOfferSkill(JobSkillEntity jobSkillEntity) {
        JobOfferSkill jobOfferSkill = new JobOfferSkill();
        jobOfferSkill.setSkillId(jobSkillEntity.getSkill().getId());
        jobOfferSkill.setName(jobSkillEntity.getSkill().getName());
        jobOfferSkill.setType(jobSkillEntity.getSkill().getSkillType());
        jobOfferSkill.setProficiency(jobSkillEntity.getExpectedProficiency());
        return jobOfferSkill;
    }
}
