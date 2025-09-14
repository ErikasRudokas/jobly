package com.jobly.repository;

import com.jobly.enums.TokenType;
import com.jobly.model.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

    Optional<TokenEntity> findByToken(String token);

    @Query("select t from TokenEntity t where t.tokenType = :tokenType and t.user.id = :userId and t.revoked = false")
    Optional<TokenEntity> findTokenByTypeAndUserId(Long userId, TokenType tokenType);

    @Query("select t from TokenEntity t where t.user.id = :userId and t.revoked = false")
    List<TokenEntity> findAllByUserId(Long userId);
}
