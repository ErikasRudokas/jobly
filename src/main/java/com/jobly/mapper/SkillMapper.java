package com.jobly.mapper;

import com.jobly.gen.model.*;
import com.jobly.model.JobSkillEntity;
import com.jobly.model.SkillAliasEntity;
import com.jobly.model.SkillEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

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

    public static SkillEntity toSkillEntity(SkillCreateRequest request, Float[] embedding) {
        SkillEntity skillEntity = new SkillEntity();
        skillEntity.setName(request.getName());
        skillEntity.setDescription(request.getDescription());
        skillEntity.setSkillType(request.getType());
        skillEntity.setEmbedding(embedding);
        return skillEntity;
    }

    public static void updateEntity(SkillEntity skillEntity, SkillUpdateRequest request) {
        if (request.getName() != null) {
            skillEntity.setName(request.getName());
        }
        if (request.getDescription() != null) {
            skillEntity.setDescription(request.getDescription());
        }
        if (request.getType() != null) {
            skillEntity.setSkillType(request.getType());
        }
    }

    public static SkillAliasEntity toAliasEntity(SkillEntity skillEntity, SkillAliasCreateRequest alias) {
        SkillAliasEntity skillAliasEntity = new SkillAliasEntity();
        skillAliasEntity.setSkill(skillEntity);
        skillAliasEntity.setAlias(alias.getValue().toLowerCase());
        return skillAliasEntity;
    }

    public static SkillAliasEntity toAliasEntity(SkillEntity skillEntity, SkillAliasUpdateRequest alias) {
        SkillAliasEntity skillAliasEntity = new SkillAliasEntity();
        skillAliasEntity.setSkill(skillEntity);
        skillAliasEntity.setAlias(alias.getValue().toLowerCase());
        return skillAliasEntity;
    }

    public static void updateAliasEntity(SkillAliasEntity existing, SkillAliasUpdateRequest alias) {
        if (Objects.nonNull(alias.getValue())) {
            existing.setAlias(alias.getValue().toLowerCase());
        }
    }

    public static Skill toSkill(SkillEntity skillEntity, List<SkillAliasEntity> aliases) {
        Skill skill = new Skill();
        skill.setId(skillEntity.getId());
        skill.setName(skillEntity.getName());
        skill.setDescription(skillEntity.getDescription());
        skill.setType(skillEntity.getSkillType());
        if (aliases != null) {
            skill.setAliases(aliases.stream().map(SkillMapper::toSkillAlias).toList());
        }
        return skill;
    }

    private static SkillAliasGetResponse toSkillAlias(SkillAliasEntity alias) {
        SkillAliasGetResponse response = new SkillAliasGetResponse();
        response.setId(alias.getId());
        response.setValue(alias.getAlias());
        return response;
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
