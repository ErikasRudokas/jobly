package com.jobly.repository;

import com.jobly.model.UserWorkExperienceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserWorkExperienceRepository extends JpaRepository<UserWorkExperienceEntity, Long> {

    boolean existsByUserId(Long userId);

    List<UserWorkExperienceEntity> findAllByUserId(Long userId);
}
