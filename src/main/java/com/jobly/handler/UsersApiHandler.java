package com.jobly.handler;

import com.jobly.gen.api.UsersApiDelegate;
import com.jobly.gen.model.CvUploadResponse;
import com.jobly.gen.model.GetUserDetailsResponse;
import com.jobly.security.service.JwtService;
import com.jobly.service.CvService;
import com.jobly.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsersApiHandler implements UsersApiDelegate {

    private final HttpServletRequest httpServletRequest;
    private final JwtService jwtService;
    private final UserService userService;
    private final CvService cvService;

    @Override
    public ResponseEntity<GetUserDetailsResponse> getUserDetails() {
        Long userId = jwtService.extractUserId(httpServletRequest);
        log.info("Getting user details for userId: {}", userId);
        return ResponseEntity.ok(userService.getUserDetails(userId));
    }

    @Override
    public ResponseEntity<CvUploadResponse> uploadUserCv(MultipartFile file) {
        Long userId = jwtService.extractUserId(httpServletRequest);
        log.info("Uploading CV for userId: {}", userId);
        return ResponseEntity.ok(cvService.uploadUserCv(userId, file));
    }

    @Override
    public ResponseEntity<Resource> downloadUserCv(Long id) {
        Long userId = jwtService.extractUserId(httpServletRequest);
        log.info("Downloading CV for userId: {}", id);
        return cvService.downloadUserCv(id, userId);
    }
}
