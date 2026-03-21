package com.jobly.service;

import com.jobly.dao.TokenDao;
import com.jobly.enums.TokenType;
import com.jobly.model.TokenEntity;
import com.jobly.model.UserEntity;
import com.jobly.security.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import static com.jobly.util.TestEntityFactory.buildToken;
import static com.jobly.util.TestEntityFactory.buildUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceTests {

    @Mock
    private TokenDao tokenDao;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private TokenService tokenService;

    @Test
    void revokeAllUserTokens_setsAllTokensRevoked() {
        Long userId = 11L;
        UserEntity user = buildUser(userId, "John", "Doe", "john@jobly.test", "john");
        TokenEntity bearer = buildToken(1L, user, "token-1", TokenType.BEARER, false);
        TokenEntity refresh = buildToken(2L, user, "token-2", TokenType.REFRESH, false);

        when(tokenDao.findAllValidTokensByUserId(userId)).thenReturn(List.of(bearer, refresh));

        tokenService.revokeAllUserTokens(userId);

        assertTrue(bearer.getRevoked());
        assertTrue(refresh.getRevoked());
        verify(tokenDao).saveAll(List.of(bearer, refresh));
    }

    @Test
    void revokeAllUserTokensByType_setsOnlyMatchingTypeRevoked() {
        Long userId = 12L;
        UserEntity user = buildUser(userId, "Jane", "Doe", "jane@jobly.test", "jane");
        TokenEntity bearer = buildToken(3L, user, "token-3", TokenType.BEARER, false);
        TokenEntity refresh = buildToken(4L, user, "token-4", TokenType.REFRESH, false);

        when(tokenDao.findAllValidTokensByUserId(userId)).thenReturn(List.of(bearer, refresh));

        tokenService.revokeAllUserTokensByType(user, TokenType.BEARER);

        assertTrue(bearer.getRevoked());
        assertFalse(refresh.getRevoked());
        verify(tokenDao).saveAll(List.of(bearer, refresh));
    }

    @Test
    void saveToken_buildsTokenEntityWithExpiry() {
        UserEntity user = buildUser(13L, "Sam", "Ray", "sam@jobly.test", "sam");
        String token = "token-5";
        Date expiration = new Date(1893456000000L);
        OffsetDateTime expectedExpiry = OffsetDateTime.ofInstant(expiration.toInstant(), ZoneOffset.UTC);

        when(jwtService.extractClaim(eq(token), any(Function.class))).thenReturn(expiration);

        tokenService.saveToken(user, token, TokenType.BEARER);

        ArgumentCaptor<TokenEntity> captor = ArgumentCaptor.forClass(TokenEntity.class);
        verify(tokenDao).save(captor.capture());
        TokenEntity saved = captor.getValue();

        assertEquals(token, saved.getToken());
        assertEquals(TokenType.BEARER, saved.getTokenType());
        assertEquals(expectedExpiry, saved.getExpiryDate());
        assertEquals(user, saved.getUser());
        assertFalse(saved.getRevoked());
    }
}

