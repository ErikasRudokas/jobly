package com.jobly.security.service;

import com.google.common.collect.ImmutableMap;
import com.jobly.enums.TokenType;
import com.jobly.model.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${auth.jwt.secret.key}")
    private String jwtSecret;

    private static final Duration BEARER_TOKEN_EXPIRATION = Duration.ofMinutes(15);
    private static final Duration REFRESH_TOKEN_EXPIRATION = Duration.ofDays(1);

    private static final String BEARER_PREFIX = "Bearer ";

    private static final String ROLE_CLAIM_KEY = "roles";
    private static final String USER_ID_CLAIM_KEY = "userId";
    private static final String TOKEN_TYPE_CLAIM_KEY = "token_type";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(UserDetails userDetails) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Map<String, Object> claims = new ImmutableMap.Builder<String, Object>()
                .put(ROLE_CLAIM_KEY, roles)
                .put(USER_ID_CLAIM_KEY, ((UserEntity) userDetails).getId())
                .put(TOKEN_TYPE_CLAIM_KEY, TokenType.BEARER.getType())
                .build();

        return buildToken(claims, userDetails, BEARER_TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new ImmutableMap.Builder<String, Object>()
                .put(TOKEN_TYPE_CLAIM_KEY, TokenType.REFRESH.getType())
                .build();

        return buildToken(claims, userDetails, REFRESH_TOKEN_EXPIRATION);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, Duration expirationTime) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime.toMillis()))
                .signWith(getSecretKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        var expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    public Long getUserIdFromToken(String token) {
        return extractClaim(token, claims -> claims.get(USER_ID_CLAIM_KEY, Long.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isRefreshToken(String token) {
        return extractClaim(token, claims -> {
            String tokenType = claims.get(TOKEN_TYPE_CLAIM_KEY, String.class);
            return TokenType.REFRESH.getType().equals(tokenType);
        });
    }

    public boolean isBearerToken(String token) {
        return extractClaim(token, claims -> {
            String tokenType = claims.get(TOKEN_TYPE_CLAIM_KEY, String.class);
            return TokenType.BEARER.getType().equals(tokenType);
        });
    }

    public Optional<String> extractTokenFromRequest(HttpServletRequest request) {
        var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return Optional.empty();
        }
        return Optional.of(authHeader.substring(BEARER_PREFIX.length()));
    }

    private Key getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
