package com.jobly.repository;

import com.jobly.enums.CvStatus;
import com.jobly.model.UserCvEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserCvRepository extends JpaRepository<UserCvEntity, Long> {

    List<UserCvEntity> findAllByUserId(Long userId);

    Optional<UserCvEntity> findByUserIdAndStatus(Long userId, CvStatus cvStatus);

    boolean existsByUserId(Long userId);

    List<UserCvEntity> findAllByUserIdAndStatusOrderByUploadedAtDesc(Long userId, CvStatus cvStatus);
}
