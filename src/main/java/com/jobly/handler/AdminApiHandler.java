package com.jobly.handler;

import com.jobly.gen.api.AdminApiDelegate;
import com.jobly.gen.model.AdminUserDetailsResponse;
import com.jobly.gen.model.AdminUserListResponse;
import com.jobly.gen.model.AdminUserStatusManageRequest;
import com.jobly.security.service.JwtService;
import com.jobly.service.AdminUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminApiHandler implements AdminApiDelegate {

    private final AdminUserService adminUserService;
    private final JwtService jwtService;
    private final HttpServletRequest httpServletRequest;

    @Override
    public ResponseEntity<AdminUserDetailsResponse> getAdminUserDetails(Long userId) {
        log.info("Getting admin user details for userId: {}", userId);
        return ResponseEntity.ok(adminUserService.getAdminUserDetails(userId));
    }

    @Override
    public ResponseEntity<AdminUserListResponse> getSystemUsers(String search, Integer offset, Integer limit) {
        log.info("Getting system users for admin");
        return ResponseEntity.ok(adminUserService.getSystemUsers(search, offset, limit));
    }

    @Override
    public ResponseEntity<Void> manageAdminUserStatus(Long userId, AdminUserStatusManageRequest adminUserStatusManageRequest) {
        Long adminUserId = jwtService.extractUserId(httpServletRequest);
        log.info("Managing user status for userId: {} by adminId: {}", userId, adminUserId);
        adminUserService.manageAdminUserStatus(userId, adminUserId, adminUserStatusManageRequest);
        return ResponseEntity.noContent().build();
    }
}
