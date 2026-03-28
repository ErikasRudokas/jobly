package com.jobly.dao;

import com.jobly.model.AdminUserActionEntity;
import com.jobly.repository.AdminUserActionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUserActionDao {

    private final AdminUserActionRepository adminUserActionRepository;

    public AdminUserActionEntity save(AdminUserActionEntity entity) {
        return adminUserActionRepository.save(entity);
    }

    public List<AdminUserActionEntity> findByTargetUserId(Long userId) {
        return adminUserActionRepository.findByTargetUserIdOrderByCreatedAtDesc(userId);
    }
}

