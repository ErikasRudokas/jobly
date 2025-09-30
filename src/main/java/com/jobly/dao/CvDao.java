package com.jobly.dao;

import com.jobly.enums.CvStatus;
import com.jobly.exception.general.NotFoundException;
import com.jobly.model.UserCvEntity;
import com.jobly.repository.UserCvRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CvDao {

    private final UserCvRepository userCvRepository;

    public UserCvEntity save(UserCvEntity entity) {
        return userCvRepository.save(entity);
    }

    public void saveAll(List<UserCvEntity> entities) {
        userCvRepository.saveAll(entities);
    }

    public List<UserCvEntity> findAllByUserId(Long userId) {
        return userCvRepository.findAllByUserId(userId);
    }

    public UserCvEntity findByUserIdAndStatus(Long userId, CvStatus cvStatus) {
        return userCvRepository.findByUserIdAndStatus(userId, cvStatus)
                .orElseThrow(() -> new NotFoundException("No active CV found for user with the id: " + userId));
    }

    public UserCvEntity findById(Long cvId) {
        return userCvRepository.findById(cvId)
                .orElseThrow(() -> new NotFoundException("CV not found with the id: " + cvId));
    }

    public UserCvEntity findOptionalByUserIdAndStatus(Long userId, CvStatus cvStatus) {
        return userCvRepository.findByUserIdAndStatus(userId, cvStatus).orElse(null);
    }
}
