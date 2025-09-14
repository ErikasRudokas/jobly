package com.jobly.security.handler;

import com.jobly.enums.TokenType;
import com.jobly.repository.TokenRepository;
import com.jobly.security.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        jwtService.extractTokenFromRequest(request).ifPresent(jwt -> {
            var storedToken = tokenRepository.findByToken(jwt).orElse(null);
            if (storedToken != null) {
                storedToken.setRevoked(Boolean.TRUE);
                tokenRepository.save(storedToken);

                if (TokenType.BEARER.equals(storedToken.getTokenType())) {
                    var userId = storedToken.getUser().getId();
                    var refreshToken = tokenRepository.findTokenByTypeAndUserId(userId, TokenType.REFRESH);
                    refreshToken.ifPresent(token -> {
                        token.setRevoked(Boolean.TRUE);
                        tokenRepository.save(token);
                    });
                }
            }
        });
    }
}
