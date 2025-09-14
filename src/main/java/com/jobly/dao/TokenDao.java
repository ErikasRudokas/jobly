package com.jobly.dao;

import com.jobly.exception.general.NotFoundException;
import com.jobly.model.TokenEntity;
import com.jobly.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenDao {

    private final TokenRepository tokenRepository;

    public List<TokenEntity> findAllValidTokensByUserId(Long userId) {
        return tokenRepository.findAllByUserId(userId);
    }

    public void saveAll(List<TokenEntity> tokens) {
        tokenRepository.saveAll(tokens);
    }

    public void save(TokenEntity tokenEntity) {
        tokenRepository.save(tokenEntity);
    }

    public TokenEntity findByToken(String token) {
        return tokenRepository.findByToken(token).orElseThrow(
                () -> {
                    log.error("Token not found: {}", token);
                    return new NotFoundException("Token not found");
                }
        );
    }
}
