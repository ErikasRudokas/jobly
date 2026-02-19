package com.jobly.repository;

import com.jobly.model.UserSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkillEntity, Long> {

    boolean existsByUserId(Long userId);

    List<UserSkillEntity> findAllByUserId(Long userId);
}
