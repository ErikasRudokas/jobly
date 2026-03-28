package com.jobly.service;

import com.jobly.dao.CvDao;
import com.jobly.dao.UserDao;
import com.jobly.enums.CvStatus;
import com.jobly.enums.Role;
import com.jobly.exception.general.BadRequestException;
import com.jobly.exception.specific.NotUniqueUsernameException;
import com.jobly.gen.model.GetUserDetailsResponse;
import com.jobly.gen.model.ModifyUserDetailsRequest;
import com.jobly.gen.model.UserRegisterRequest;
import com.jobly.gen.model.UserRegisterResponse;
import com.jobly.mapper.UserMapper;
import com.jobly.model.UserCvEntity;
import com.jobly.model.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final CvDao cvDao;

    public Optional<UserEntity> findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userDao.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userDao.existsByDisplayName(username);
    }

    public UserRegisterResponse save(UserRegisterRequest registerRequest) {
        var user = UserMapper.toUserEntityFromRegisterRequest(registerRequest);
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        var userEntity = userDao.save(user);
        return UserMapper.toUserRegisterResponse(userEntity);
    }

    public UserEntity findById(Long userId) {
        return userDao.findById(userId);
    }

    public boolean isCurrentUserEmployer(Long userId) {
        return userDao.findById(userId).getRole().equals(Role.EMPLOYER);
    }

    public GetUserDetailsResponse updateUserDetails(Long userId, ModifyUserDetailsRequest request) {
        if (request == null) {
            throw new BadRequestException("Update request is required.");
        }

        UserEntity user = userDao.findById(userId);

        String username = request.getUsername();
        if (username != null) {
            if (username.isBlank()) {
                throw new BadRequestException("Username cannot be blank.");
            }
            if (!username.equals(user.getDisplayName()) && userDao.existsByDisplayName(username)) {
                throw new NotUniqueUsernameException("Username '" + username + "' is already in use.");
            }
            user.setDisplayName(username);
        }

        String firstName = request.getFirstName();
        if (firstName != null) {
            if (firstName.isBlank()) {
                throw new BadRequestException("First name cannot be blank.");
            }
            user.setFirstName(firstName);
        }

        String lastName = request.getLastName();
        if (lastName != null) {
            if (lastName.isBlank()) {
                throw new BadRequestException("Last name cannot be blank.");
            }
            user.setLastName(lastName);
        }

        UserEntity savedUser = userDao.save(user);
        UserCvEntity activeCv = cvDao.findOptionalByUserIdAndStatus(userId, CvStatus.ACTIVE);
        return UserMapper.toGetUserDetailsResponse(savedUser, activeCv);
    }
}
