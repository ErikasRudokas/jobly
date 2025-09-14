package com.jobly.service;

import com.jobly.dao.TokenDao;
import com.jobly.enums.TokenType;
import com.jobly.model.TokenEntity;
import com.jobly.model.UserEntity;
import com.jobly.security.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final TokenDao tokenDao;
    private final JwtService jwtService;

    public TokenEntity findByToken(String token) {
        return tokenDao.findByToken(token);
    }

    public void revokeAllUserTokens(Long userId) {
        var tokens = tokenDao.findAllValidTokensByUserId(userId);
        tokens.forEach(token -> token.setRevoked(Boolean.TRUE));
        tokenDao.saveAll(tokens);
    }

    public void revokeAllUserTokensByType(UserEntity user, TokenType tokenType) {
        var tokens = tokenDao.findAllValidTokensByUserId(user.getId());
        tokens.stream()
                .filter(token -> token.getTokenType().equals(tokenType))
                .forEach(token -> token.setRevoked(Boolean.TRUE));
        tokenDao.saveAll(tokens);
    }

    public void saveToken(UserEntity user, String token, TokenType tokenType) {
        TokenEntity tokenEntity = TokenEntity.builder()
                .token(token)
                .tokenType(tokenType)
                .revoked(Boolean.FALSE)
                .expiryDate(jwtService.extractClaim(token, Claims::getExpiration).toInstant().atOffset(ZoneOffset.UTC))
                .user(user)
                .build();

        tokenDao.save(tokenEntity);
    }
}
