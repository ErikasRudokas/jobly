package com.jobly.repository;

import com.jobly.enums.Role;
import com.jobly.enums.TokenType;
import com.jobly.model.TokenEntity;
import com.jobly.model.UserEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class TokenRepositoryTests {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findTokenByTypeAndUserId_returnsUnrevokedToken() {
        UserEntity user = persistUser("user@jobly.test", "user");
        TokenEntity refresh = persistToken(user, "refresh", TokenType.REFRESH, false);
        persistToken(user, "revoked", TokenType.REFRESH, true);

        entityManager.flush();

        Optional<TokenEntity> found = tokenRepository.findTokenByTypeAndUserId(user.getId(), TokenType.REFRESH);

        assertTrue(found.isPresent());
        assertEquals(refresh.getId(), found.get().getId());
    }

    @Test
    void findAllByUserId_returnsOnlyUnrevokedTokens() {
        UserEntity user = persistUser("user2@jobly.test", "user2");
        TokenEntity bearer = persistToken(user, "bearer", TokenType.BEARER, false);
        persistToken(user, "revoked", TokenType.BEARER, true);

        entityManager.flush();

        List<TokenEntity> tokens = tokenRepository.findAllByUserId(user.getId());

        assertEquals(1, tokens.size());
        assertEquals(bearer.getId(), tokens.get(0).getId());
    }

    private UserEntity persistUser(String email, String displayName) {
        UserEntity user = UserEntity.builder()
                .firstName("Test")
                .lastName("User")
                .displayName(displayName)
                .email(email)
                .passwordHash("hash")
                .role(Role.USER)
                .suspended(false)
                .build();
        entityManager.persist(user);
        return user;
    }

    private TokenEntity persistToken(UserEntity user, String tokenValue, TokenType tokenType, boolean revoked) {
        TokenEntity token = TokenEntity.builder()
                .token(tokenValue)
                .tokenType(tokenType)
                .revoked(revoked)
                .user(user)
                .expiryDate(OffsetDateTime.now())
                .build();
        entityManager.persist(token);
        return token;
    }
}
