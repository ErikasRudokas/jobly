package com.jobly.dao;

import com.jobly.exception.general.NotFoundException;
import com.jobly.model.UserEntity;
import com.jobly.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDao {

    private final UserRepository userRepository;

    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByDisplayName(String username) {
        return userRepository.existsByDisplayName(username);
    }

    public UserEntity save(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    public UserEntity findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }

    public List<UserEntity> findSystemUsersBySearch(String search, Integer offset, Integer limit) {
        int defaultOffset = offset != null ? offset : 0;
        int defaultLimit = limit != null ? limit : 10;
        String defaultSearch = search != null ? search : "";
        return userRepository.findSystemUsersBySearch(defaultSearch, defaultLimit, defaultOffset);
    }

    public Integer countSystemUsersBySearch(String search) {
        String defaultSearch = search != null ? search : "";
        return userRepository.countSystemUsersBySearch(defaultSearch);
    }
}
