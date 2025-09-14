package com.jobly.handler;

import com.jobly.gen.api.AuthApiDelegate;
import com.jobly.gen.model.*;
import com.jobly.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthApiHandler implements AuthApiDelegate {

    private final AuthenticationService authenticationService;

    @Override
    public ResponseEntity<UserLoginResponse> login(UserLoginRequest userLoginRequest) {
        log.info("Login process started for user: {}", userLoginRequest.getEmail());
        var loginResponse = authenticationService.login(userLoginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @Override
    public ResponseEntity<UserRegisterResponse> register(UserRegisterRequest userRegisterRequest) {
        log.info("Registration process started for user: {}", userRegisterRequest.getEmail());
        var registerResponse = authenticationService.register(userRegisterRequest);
        return ResponseEntity
                .created(URI.create("/api/v1/user/" + registerResponse.getId()))
                .body(registerResponse);
    }

    @Override
    public ResponseEntity<RefreshTokenResponse> refreshToken(RefreshTokenRequest refreshTokenRequest) {
        var refreshResponse = authenticationService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(refreshResponse);
    }
}
