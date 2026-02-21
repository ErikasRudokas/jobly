package com.jobly.mapper;

import com.jobly.gen.model.CVDataStatus;
import com.jobly.gen.model.SaveUserSkillRequest;
import com.jobly.gen.model.UserSkill;
import com.jobly.gen.model.UserSkillBase;
import com.jobly.model.SkillEntity;
import com.jobly.model.UserEntity;
import com.jobly.model.UserSkillEntity;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSkillMapper {

    public static UserSkillEntity fromParsedDataToEntity(SkillEntity skill, UserEntity userEntity) {
        UserSkillEntity userSkillEntity = new UserSkillEntity();
        userSkillEntity.setUser(userEntity);
        userSkillEntity.setSkill(skill);
        userSkillEntity.setStatus(CVDataStatus.AI_PARSED);
        return userSkillEntity;
    }

    public static UserSkill fromEntityToResponse(UserSkillEntity userSkillEntity) {
        UserSkill userSkill = new UserSkill();
        userSkill.setId(userSkillEntity.getId());
        userSkill.setStatus(userSkillEntity.getStatus());
        userSkill.setProficiencyLevel(userSkillEntity.getProficiencyLevel());
        userSkill.setSkill(SkillMapper.toSkillDetails(userSkillEntity.getSkill()));
        return userSkill;
    }

    public static UserSkillEntity fromSaveRequestToEntity(SaveUserSkillRequest skill, UserEntity userEntity, SkillEntity skillEntity) {
        UserSkillEntity userSkillEntity = new UserSkillEntity();
        userSkillEntity.setId(skill.getId());
        userSkillEntity.setStatus(CVDataStatus.USER_REVIEWED);
        userSkillEntity.setProficiencyLevel(skill.getProficiencyLevel());
        userSkillEntity.setUser(userEntity);
        userSkillEntity.setSkill(skillEntity);
        return userSkillEntity;
    }

    public static void updateEntity(UserSkillEntity existing, SaveUserSkillRequest skillRequest) {
        existing.setProficiencyLevel(skillRequest.getProficiencyLevel());
        existing.setStatus(CVDataStatus.USER_REVIEWED);
    }

    public static UserSkillBase fromEntityToBaseResponse(UserSkillEntity userSkillEntity) {
        UserSkillBase userSkillBase = new UserSkillBase();
        userSkillBase.setStatus(userSkillEntity.getStatus());
        userSkillBase.setProficiencyLevel(userSkillEntity.getProficiencyLevel());
        userSkillBase.setSkill(SkillMapper.toSkillDetails(userSkillEntity.getSkill()));
        return userSkillBase;
    }
}
