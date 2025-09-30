package com.jobly.mapper;

import com.jobly.enums.Role;
import com.jobly.gen.model.*;
import com.jobly.model.UserCvEntity;
import com.jobly.model.UserEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserEntity toUserEntityFromRegisterRequest(UserRegisterRequest registerRequest) {
        return UserEntity.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .role(Role.ADMINISTRATOR)
                .build();
    }

    public static UserRegisterResponse toUserRegisterResponse(UserEntity userEntity) {
        return new UserRegisterResponse()
                .id(userEntity.getId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail());
    }

    public static Creator toCreator(UserEntity userEntity) {
        var creator = new Creator();
        creator.setFirstName(userEntity.getFirstName());
        creator.setLastName(userEntity.getLastName());
        return creator;
    }

    public static GetUserDetailsResponse toGetUserDetailsResponse(UserEntity userEntity, UserCvEntity userCv) {
        var response = new GetUserDetailsResponse();
        response.setId(userEntity.getId());
        response.setFirstName(userEntity.getFirstName());
        response.setLastName(userEntity.getLastName());
        response.setUsername(userEntity.getUsername());
        response.setEmail(userEntity.getEmail());
        response.setCvId(Optional.ofNullable(userCv).map(UserCvEntity::getId).orElse(null));
        return response;
    }

    public static Applicant toApplicant(UserEntity userEntity) {
        var applicant = new Applicant();
        applicant.setId(userEntity.getId());
        applicant.setFirstName(userEntity.getFirstName());
        applicant.setLastName(userEntity.getLastName());
        applicant.setEmail(userEntity.getEmail());
        return applicant;
    }
}
