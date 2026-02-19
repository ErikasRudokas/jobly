package com.jobly.mapper;

import com.jobly.gen.api.parser.data.Education;
import com.jobly.gen.model.CVDataStatus;
import com.jobly.gen.model.SaveUserEducationRequest;
import com.jobly.gen.model.UserEducation;
import com.jobly.model.UserEducationEntity;
import com.jobly.model.UserEntity;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserEducationMapper {

    public static UserEducationEntity fromParsedDataToEntity(Education education, UserEntity userEntity) {
        UserEducationEntity userEducationEntity = new UserEducationEntity();
        userEducationEntity.setUser(userEntity);
        userEducationEntity.setInstitutionName(education.getInstitution());
        userEducationEntity.setDegree(education.getDegree());
        userEducationEntity.setStatus(CVDataStatus.AI_PARSED);
        return userEducationEntity;
    }

    public static UserEducation fromEntityToResponse(UserEducationEntity userEducationEntity) {
        UserEducation userEducation = new UserEducation();
        userEducation.setId(userEducationEntity.getId());
        userEducation.setInstitutionName(userEducationEntity.getInstitutionName());
        userEducation.setDegree(userEducationEntity.getDegree());
        userEducation.setStatus(userEducationEntity.getStatus());
        userEducation.setStartDate(userEducationEntity.getStartDate());
        userEducation.setEndDate(userEducationEntity.getEndDate());
        return userEducation;
    }

    public static UserEducationEntity fromSaveRequestToEntity(SaveUserEducationRequest education, UserEntity userEntity) {
        UserEducationEntity userEducationEntity = new UserEducationEntity();
        userEducationEntity.setId(education.getId());
        userEducationEntity.setInstitutionName(education.getInstitutionName());
        userEducationEntity.setDegree(education.getDegree());
        userEducationEntity.setStartDate(education.getStartDate());
        userEducationEntity.setEndDate(education.getEndDate());
        userEducationEntity.setStatus(CVDataStatus.USER_REVIEWED);
        userEducationEntity.setUser(userEntity);
        return userEducationEntity;
    }

    public static void updateEntity(UserEducationEntity existing, SaveUserEducationRequest education) {
        existing.setInstitutionName(education.getInstitutionName());
        existing.setDegree(education.getDegree());
        existing.setStartDate(education.getStartDate());
        existing.setEndDate(education.getEndDate());
        existing.setStatus(CVDataStatus.USER_REVIEWED);
    }
}
