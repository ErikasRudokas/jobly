package com.jobly.mapper;

import com.jobly.enums.Role;
import com.jobly.gen.model.Creator;
import com.jobly.gen.model.UserRegisterRequest;
import com.jobly.gen.model.UserRegisterResponse;
import com.jobly.model.UserEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
}
