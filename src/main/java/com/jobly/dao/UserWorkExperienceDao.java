package com.jobly.dao;

import com.jobly.model.UserWorkExperienceEntity;
import com.jobly.repository.UserWorkExperienceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserWorkExperienceDao {

    private final UserWorkExperienceRepository userWorkExperienceRepository;

    public List<UserWorkExperienceEntity> saveAll(List<UserWorkExperienceEntity> workExperienceEntities) {
        return userWorkExperienceRepository.saveAll(workExperienceEntities);
    }

    public boolean existsByUserId(Long userId) {
        return userWorkExperienceRepository.existsByUserId(userId);
    }

    public List<UserWorkExperienceEntity> findAllByUserId(Long userId) {
        return userWorkExperienceRepository.findAllByUserId(userId);
    }

    public void deleteAll(List<UserWorkExperienceEntity> workExperienceToDelete) {
        userWorkExperienceRepository.deleteAll(workExperienceToDelete);
    }
}
