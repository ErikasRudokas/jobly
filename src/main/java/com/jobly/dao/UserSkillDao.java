package com.jobly.dao;

import com.jobly.model.UserSkillEntity;
import com.jobly.repository.UserSkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserSkillDao {

    private final UserSkillRepository userSkillRepository;

    public List<UserSkillEntity> saveAll(List<UserSkillEntity> userSkillEntities) {
        return userSkillRepository.saveAll(userSkillEntities);
    }

    public boolean existsByUserId(Long userId) {
        return userSkillRepository.existsByUserId(userId);
    }

    public List<UserSkillEntity> findAllByUserId(Long userId) {
        return userSkillRepository.findAllByUserId(userId);
    }

    public void deleteAll(List<UserSkillEntity> skillsToDelete) {
        userSkillRepository.deleteAll(skillsToDelete);
    }
}
