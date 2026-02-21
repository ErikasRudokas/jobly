package com.jobly.mapper;

import com.jobly.gen.api.parser.data.WorkExperience;
import com.jobly.gen.model.CVDataStatus;
import com.jobly.gen.model.SaveUserWorkExperienceRequest;
import com.jobly.gen.model.UserWorkExperience;
import com.jobly.gen.model.UserWorkExperienceBase;
import com.jobly.model.UserEntity;
import com.jobly.model.UserWorkExperienceEntity;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserWorkExperienceMapper {


    public static UserWorkExperienceEntity fromParsedDataToEntity(WorkExperience workExperience, UserEntity userEntity) {
        UserWorkExperienceEntity userWorkExperienceEntity = new UserWorkExperienceEntity();
        userWorkExperienceEntity.setCompanyName(workExperience.getCompany());
        userWorkExperienceEntity.setDesignation(workExperience.getDesignation());
        userWorkExperienceEntity.setUser(userEntity);
        userWorkExperienceEntity.setStatus(CVDataStatus.AI_PARSED);
        return userWorkExperienceEntity;
    }

    public static UserWorkExperience fromEntityToResponse(UserWorkExperienceEntity userWorkExperienceEntity) {
        UserWorkExperience userWorkExperience = new UserWorkExperience();
        userWorkExperience.setId(userWorkExperienceEntity.getId());
        userWorkExperience.setCompanyName(userWorkExperienceEntity.getCompanyName());
        userWorkExperience.setDesignation(userWorkExperienceEntity.getDesignation());
        userWorkExperience.setStatus(userWorkExperienceEntity.getStatus());
        userWorkExperience.setStartDate(userWorkExperienceEntity.getStartDate());
        userWorkExperience.setEndDate(userWorkExperienceEntity.getEndDate());
        return userWorkExperience;
    }

    public static UserWorkExperienceEntity fromSaveRequestToEntity(SaveUserWorkExperienceRequest workExperience, UserEntity userEntity) {
        UserWorkExperienceEntity userWorkExperienceEntity = new UserWorkExperienceEntity();
        userWorkExperienceEntity.setId(workExperience.getId());
        userWorkExperienceEntity.setCompanyName(workExperience.getCompanyName());
        userWorkExperienceEntity.setDesignation(workExperience.getDesignation());
        userWorkExperienceEntity.setStartDate(workExperience.getStartDate());
        userWorkExperienceEntity.setEndDate(workExperience.getEndDate());
        userWorkExperienceEntity.setStatus(CVDataStatus.USER_REVIEWED);
        userWorkExperienceEntity.setUser(userEntity);
        return userWorkExperienceEntity;
    }

    public static void updateEntity(UserWorkExperienceEntity existing, SaveUserWorkExperienceRequest workExperience) {
        existing.setDesignation(workExperience.getDesignation());
        existing.setCompanyName(workExperience.getCompanyName());
        existing.setStartDate(workExperience.getStartDate());
        existing.setEndDate(workExperience.getEndDate());
        existing.setStatus(CVDataStatus.USER_REVIEWED);
    }

    public static UserWorkExperienceBase fromEntityToBaseResponse(UserWorkExperienceEntity userWorkExperienceEntity) {
        UserWorkExperienceBase userWorkExperienceBase = new UserWorkExperienceBase();
        userWorkExperienceBase.setCompanyName(userWorkExperienceEntity.getCompanyName());
        userWorkExperienceBase.setDesignation(userWorkExperienceEntity.getDesignation());
        userWorkExperienceBase.setStartDate(userWorkExperienceEntity.getStartDate());
        userWorkExperienceBase.setEndDate(userWorkExperienceEntity.getEndDate());
        userWorkExperienceBase.setStatus(userWorkExperienceEntity.getStatus());
        return userWorkExperienceBase;
    }
}
