package com.jobly.repository;

import com.jobly.model.AdminUserActionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminUserActionRepository extends JpaRepository<AdminUserActionEntity, Long> {

    @Query("SELECT action FROM AdminUserActionEntity action WHERE action.targetUser.id = :userId ORDER BY action.createdAt DESC")
    List<AdminUserActionEntity> findByTargetUserIdOrderByCreatedAtDesc(Long userId);
}

