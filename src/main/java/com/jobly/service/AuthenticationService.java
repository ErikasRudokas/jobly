package com.jobly.service;

import com.jobly.enums.TokenType;
import com.jobly.exception.specific.InvalidCredentialsException;
import com.jobly.exception.specific.NotUniqueEmailException;
import com.jobly.exception.specific.NotUniqueUsernameException;
import com.jobly.exception.specific.TokenRevokedException;
import com.jobly.gen.model.*;
import com.jobly.model.UserEntity;
import com.jobly.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenService tokenService;

    public UserLoginResponse login(UserLoginRequest userLoginRequest) {
        log.info("Login process started for user: {}", userLoginRequest.getEmail());
        var user = userService.findByEmail(userLoginRequest.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials."));

        authenticateUser(userLoginRequest, user);

        log.info("Generating tokens for user with id: {}", user.getId());
        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        revokeAndSaveNewTokens(user, accessToken, refreshToken);

        return new UserLoginResponse()
                .accessToken(accessToken)
                .refreshToken(refreshToken);
    }

    public UserRegisterResponse register(UserRegisterRequest userRegisterRequest) {
        var email = userRegisterRequest.getEmail();
        var username = userRegisterRequest.getUsername();

        validateRegistrationAllowed(email, username);
        return userService.save(userRegisterRequest);
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        var refreshToken = refreshTokenRequest.getRefreshToken();

        var email = jwtService.extractUsername(refreshToken);
        UserEntity user = userService.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User with email {} not found during token refresh.", email);
                    return new InvalidCredentialsException("Problem refreshing token.");
                });

        var isTokenValid = !tokenService.findByToken(refreshToken).getRevoked();

        if (!jwtService.isTokenValid(refreshToken, user) || !isTokenValid) {
            log.error("Refresh token is invalid or has been revoked for user with id: {}", user.getId());
            throw new TokenRevokedException("Refresh token has been revoked by the system.");
        }

        var accessToken = jwtService.generateToken(user);
        tokenService.revokeAllUserTokensByType(user, TokenType.BEARER);
        tokenService.saveToken(user, accessToken, TokenType.BEARER);

        return new RefreshTokenResponse()
                .accessToken(accessToken);
    }

    private void authenticateUser(UserLoginRequest userLoginRequest, UserEntity user) {
        if (!passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())) {
            log.error("Password does not match stored password for user with id: {}", user.getId());
            throw new InvalidCredentialsException("Invalid credentials.");
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginRequest.getEmail(), userLoginRequest.getPassword()));
    }

    private void revokeAndSaveNewTokens(UserEntity user, String accessToken, String refreshToken) {
        tokenService.revokeAllUserTokens(user.getId());
        tokenService.saveToken(user, accessToken, TokenType.BEARER);
        tokenService.saveToken(user, refreshToken, TokenType.REFRESH);
    }

    private void validateRegistrationAllowed(String email, String username) {
        if (userService.existsByEmail(email)) {
            throw new NotUniqueEmailException("Email '" + email + "' is already in use.");
        }

        if (userService.existsByUsername(username)) {
            throw new NotUniqueUsernameException("Username '" + username + "' is already in use.");
        }
    }
}
