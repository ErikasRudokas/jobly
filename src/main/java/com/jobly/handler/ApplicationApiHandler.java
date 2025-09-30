package com.jobly.handler;

import com.jobly.gen.api.ApplicationsApiDelegate;
import com.jobly.gen.model.*;
import com.jobly.security.service.JwtService;
import com.jobly.service.ApplicationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationApiHandler implements ApplicationsApiDelegate {

    private final HttpServletRequest httpServletRequest;
    private final JwtService jwtService;
    private final ApplicationService applicationService;

    @Override
    public ResponseEntity<Void> cancelApplication(Long id) {
        var userId = jwtService.extractUserId(httpServletRequest);
        log.info("Cancelling application with id: {} for user with id: {}", id, userId);
        applicationService.cancelApplication(id, userId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<MyApplication> getMyApplication(Long id) {
        var userId = jwtService.extractUserId(httpServletRequest);
        log.info("Getting application with id: {} for user with id: {}", id, userId);
        return ResponseEntity.ok(applicationService.getMyApplication(id, userId));
    }

    @Override
    public ResponseEntity<GetMyApplicationsResponse> getMyApplications() {
        var userId = jwtService.extractUserId(httpServletRequest);
        log.info("Getting all of applications for user with id: {}", userId);
        return ResponseEntity.ok(applicationService.getMyApplications(userId));
    }

    @Override
    public ResponseEntity<Void> manageApplication(Long id, ApplicationManageRequest applicationManageRequest) {
        var userId = jwtService.extractUserId(httpServletRequest);
        log.info("User with id: {} is managing application with id: {}",userId,id);
        applicationService.manageApplication(id, userId, applicationManageRequest);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Application> updateApplication(Long id, ApplicationUpdateRequest applicationUpdateRequest) {
        var userId = jwtService.extractUserId(httpServletRequest);
        log.info("Updating application with id: {} for user with id: {}", id, userId);
        return ResponseEntity.ok(applicationService.updateApplication(id, userId, applicationUpdateRequest));
    }
}
