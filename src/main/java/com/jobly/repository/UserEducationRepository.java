package com.jobly.repository;

import com.jobly.model.UserEducationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserEducationRepository extends JpaRepository<UserEducationEntity, Long> {

    boolean existsByUserId(Long userId);

    List<UserEducationEntity> findAllByUserId(Long userId);
}
