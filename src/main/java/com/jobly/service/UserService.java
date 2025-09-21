package com.jobly.service;

import com.jobly.dao.UserDao;
import com.jobly.gen.model.UserRegisterRequest;
import com.jobly.gen.model.UserRegisterResponse;
import com.jobly.mapper.UserMapper;
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

    public Optional<UserEntity> findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    public Optional<UserEntity> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userDao.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userDao.existsByUsername(username);
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
}
