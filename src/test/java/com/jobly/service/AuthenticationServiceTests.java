package com.jobly.service;

import com.jobly.enums.TokenType;
import com.jobly.exception.specific.*;
import com.jobly.gen.model.*;
import com.jobly.model.TokenEntity;
import com.jobly.model.UserEntity;
import com.jobly.security.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.jobly.util.TestEntityFactory.buildToken;
import static com.jobly.util.TestEntityFactory.buildUserWithPassword;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTests {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void login_successReturnsTokensAndSaves() {
        UserLoginRequest request = new UserLoginRequest()
                .email("john@jobly.test")
                .password("secret");
        UserEntity user = buildUserWithPassword(1L, "John", "Doe", "john@jobly.test", "john", "encoded");

        when(userService.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(jwtService.generateToken(user)).thenReturn("access");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh");

        UserLoginResponse response = authenticationService.login(request);

        assertEquals("access", response.getAccessToken());
        assertEquals("refresh", response.getRefreshToken());
        verify(tokenService).revokeAllUserTokens(user.getId());
        verify(tokenService).saveToken(user, "access", TokenType.BEARER);
        verify(tokenService).saveToken(user, "refresh", TokenType.REFRESH);
    }

    @Test
    void login_missingUserThrowsInvalidCredentials() {
        UserLoginRequest request = new UserLoginRequest()
                .email("missing@jobly.test")
                .password("secret");

        when(userService.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authenticationService.login(request));
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService, never()).revokeAllUserTokens(any());
    }

    @Test
    void login_passwordMismatchThrowsInvalidCredentials() {
        UserLoginRequest request = new UserLoginRequest()
                .email("john@jobly.test")
                .password("wrong");
        UserEntity user = buildUserWithPassword(2L, "John", "Doe", "john@jobly.test", "john", "encoded");

        when(userService.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authenticationService.login(request));
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService, never()).saveToken(any(), any(), any());
    }

    @Test
    void register_emailAlreadyUsedThrows() {
        UserRegisterRequest request = new UserRegisterRequest()
                .email("taken@jobly.test")
                .username("taken");

        when(userService.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(NotUniqueEmailException.class, () -> authenticationService.register(request));
    }

    @Test
    void register_usernameAlreadyUsedThrows() {
        UserRegisterRequest request = new UserRegisterRequest()
                .email("free@jobly.test")
                .username("taken");

        when(userService.existsByEmail(request.getEmail())).thenReturn(false);
        when(userService.existsByUsername(request.getUsername())).thenReturn(true);

        assertThrows(NotUniqueUsernameException.class, () -> authenticationService.register(request));
    }

    @Test
    void register_successDelegatesToUserService() {
        UserRegisterRequest request = new UserRegisterRequest()
                .firstName("Jane")
                .lastName("Roe")
                .username("jane")
                .email("jane@jobly.test")
                .password("secret");

        UserRegisterResponse saved = new UserRegisterResponse()
                .id(10L)
                .firstName("Jane")
                .lastName("Roe")
                .username("jane")
                .email("jane@jobly.test");

        when(userService.existsByEmail(request.getEmail())).thenReturn(false);
        when(userService.existsByUsername(request.getUsername())).thenReturn(false);
        when(userService.save(request)).thenReturn(saved);

        UserRegisterResponse response = authenticationService.register(request);

        assertEquals(saved.getId(), response.getId());
        assertEquals(saved.getEmail(), response.getEmail());
    }

    @Test
    void refreshToken_userNotFoundThrowsInvalidCredentials() {
        RefreshTokenRequest request = new RefreshTokenRequest().refreshToken("refresh");

        when(jwtService.extractUsername(request.getRefreshToken())).thenReturn("missing@jobly.test");
        when(userService.findByEmail("missing@jobly.test")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authenticationService.refreshToken(request));
    }

    @Test
    void refreshToken_revokedThrowsTokenRevoked() {
        RefreshTokenRequest request = new RefreshTokenRequest().refreshToken("refresh");
        UserEntity user = buildUserWithPassword(3L, "John", "Doe", "john@jobly.test", "john", "encoded");
        TokenEntity tokenEntity = buildToken(1L, user, request.getRefreshToken(), TokenType.REFRESH, true);

        when(jwtService.extractUsername(request.getRefreshToken())).thenReturn(user.getEmail());
        when(userService.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(tokenService.findByToken(request.getRefreshToken())).thenReturn(tokenEntity);
        when(jwtService.isTokenValid(request.getRefreshToken(), user)).thenReturn(true);

        assertThrows(TokenRevokedException.class, () -> authenticationService.refreshToken(request));
        verify(tokenService, never()).saveToken(any(), any(), any());
    }

    @Test
    void refreshToken_successGeneratesAccessToken() {
        RefreshTokenRequest request = new RefreshTokenRequest().refreshToken("refresh");
        UserEntity user = buildUserWithPassword(4L, "John", "Doe", "john@jobly.test", "john", "encoded");
        TokenEntity tokenEntity = buildToken(2L, user, request.getRefreshToken(), TokenType.REFRESH, false);

        when(jwtService.extractUsername(request.getRefreshToken())).thenReturn(user.getEmail());
        when(userService.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(tokenService.findByToken(request.getRefreshToken())).thenReturn(tokenEntity);
        when(jwtService.isTokenValid(request.getRefreshToken(), user)).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("new-access");

        RefreshTokenResponse response = authenticationService.refreshToken(request);

        assertEquals("new-access", response.getAccessToken());
        verify(tokenService).revokeAllUserTokensByType(user, TokenType.BEARER);
        verify(tokenService).saveToken(user, "new-access", TokenType.BEARER);
    }

    @Test
    void login_suspendedUserThrows() {
        UserLoginRequest request = new UserLoginRequest()
                .email("suspended@jobly.test")
                .password("secret");
        UserEntity user = buildUserWithPassword(5L, "Sue", "Stone", "suspended@jobly.test", "sue", "encoded");
        user.setSuspended(true);

        when(userService.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        assertThrows(SuspendedUserException.class, () -> authenticationService.login(request));
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService, never()).saveToken(any(), any(), any());
    }
}
