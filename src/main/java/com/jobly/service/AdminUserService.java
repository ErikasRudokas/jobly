package com.jobly.service;

import com.jobly.dao.AdminUserActionDao;
import com.jobly.dao.UserDao;
import com.jobly.enums.Role;
import com.jobly.exception.general.BadRequestException;
import com.jobly.exception.general.NotFoundException;
import com.jobly.gen.model.*;
import com.jobly.model.AdminUserActionEntity;
import com.jobly.model.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserService {

    private final UserDao userDao;
    private final AdminUserActionDao adminUserActionDao;

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public AdminUserListResponse getSystemUsers(String search, Integer offset, Integer limit) {
        var users = userDao.findSystemUsersBySearch(search, offset, limit)
                .stream()
                .map(this::toAdminUserListItem)
                .toList();
        Integer total = userDao.countSystemUsersBySearch(search);
        return new AdminUserListResponse()
                .users(users)
                .total(total);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public AdminUserDetailsResponse getAdminUserDetails(Long userId) {
        UserEntity user = userDao.findById(userId);
        ensureSystemUser(user);
        List<AdminUserAction> actions = adminUserActionDao.findByTargetUserId(userId).stream()
                .map(this::toAdminUserAction)
                .toList();

        return new AdminUserDetailsResponse()
                .user(toAdminUserDetails(user))
                .actions(actions);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public void manageAdminUserStatus(Long targetUserId, Long adminUserId, AdminUserStatusManageRequest request) {
        if (request == null || request.getAction() == null || request.getComment() == null) {
            throw new BadRequestException("Action and comment are required.");
        }
        if (request.getComment().isBlank()) {
            throw new BadRequestException("Comment cannot be blank.");
        }

        UserEntity targetUser = userDao.findById(targetUserId);
        ensureSystemUser(targetUser);

        boolean currentlySuspended = Boolean.TRUE.equals(targetUser.getSuspended());
        switch (request.getAction()) {
            case SUSPEND -> {
                if (currentlySuspended) {
                    throw new BadRequestException("User is already suspended.");
                }
                targetUser.setSuspended(true);
            }
            case RESTORE -> {
                if (!currentlySuspended) {
                    throw new BadRequestException("User is not suspended.");
                }
                targetUser.setSuspended(false);
            }
            default -> throw new BadRequestException("Invalid action.");
        }

        userDao.save(targetUser);
        UserEntity adminUser = userDao.findById(adminUserId);

        AdminUserActionEntity actionEntity = AdminUserActionEntity.builder()
                .action(request.getAction())
                .comment(request.getComment())
                .targetUser(targetUser)
                .performedBy(adminUser)
                .build();
        adminUserActionDao.save(actionEntity);
    }

    private void ensureSystemUser(UserEntity user) {
        if (user.getRole() == Role.ADMINISTRATOR) {
            throw new NotFoundException("User not found with id: " + user.getId());
        }
    }

    private AdminUserListItem toAdminUserListItem(UserEntity user) {
        return new AdminUserListItem()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getDisplayName())
                .email(user.getEmail())
                .role(toAdminUserRole(user.getRole()))
                .suspended(Boolean.TRUE.equals(user.getSuspended()));
    }

    private AdminUserDetails toAdminUserDetails(UserEntity user) {
        return new AdminUserDetails()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getDisplayName())
                .email(user.getEmail())
                .role(toAdminUserRole(user.getRole()))
                .suspended(Boolean.TRUE.equals(user.getSuspended()));
    }

    private AdminUserAction toAdminUserAction(AdminUserActionEntity actionEntity) {
        AdminUserActionPerformedBy performedBy = new AdminUserActionPerformedBy()
                .id(actionEntity.getPerformedBy().getId())
                .firstName(actionEntity.getPerformedBy().getFirstName())
                .lastName(actionEntity.getPerformedBy().getLastName())
                .username(actionEntity.getPerformedBy().getDisplayName());

        return new AdminUserAction()
                .id(actionEntity.getId())
                .action(actionEntity.getAction())
                .comment(actionEntity.getComment())
                .createdAt(actionEntity.getCreatedAt())
                .performedBy(performedBy);
    }

    private AdminUserRole toAdminUserRole(Role role) {
        return Role.EMPLOYER.equals(role) ? AdminUserRole.EMPLOYER : AdminUserRole.USER;
    }
}

