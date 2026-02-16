package com.jobly.dao;

import com.jobly.model.UserEducationEntity;
import com.jobly.repository.UserEducationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEducationDao {

    private final UserEducationRepository userEducationRepository;

    public List<UserEducationEntity> saveAll(List<UserEducationEntity> educationEntities) {
        return userEducationRepository.saveAll(educationEntities);
    }

    public boolean existsByUserId(Long userId) {
        return userEducationRepository.existsByUserId(userId);
    }

    public List<UserEducationEntity> findAllByUserId(Long userId) {
        return userEducationRepository.findAllByUserId(userId);
    }

    public void deleteAll(List<UserEducationEntity> educationsToDelete) {
        userEducationRepository.deleteAll(educationsToDelete);
    }
}
