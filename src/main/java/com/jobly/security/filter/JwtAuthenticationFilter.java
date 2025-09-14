package com.jobly.security.filter;

import com.jobly.exception.specific.IncorrectTokenUsageException;
import com.jobly.model.TokenEntity;
import com.jobly.repository.TokenRepository;
import com.jobly.security.service.CustomUserDetailsService;
import com.jobly.security.service.JwtService;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {
        jwtService.extractTokenFromRequest(request).ifPresent(jwt -> {
            var email = jwtService.extractUsername(jwt);
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtService.isRefreshToken(jwt)) {
                    throw new IncorrectTokenUsageException("Refresh token cannot be used to access resources");
                }

                authenticateUser(request, jwt, userDetails);
            }
        });

        filterChain.doFilter(request, response);
    }

    private void authenticateUser(HttpServletRequest request, String jwt, UserDetails userDetails) {
        var isTokenRevoked = getIsTokenRevoked(jwt);

        if (jwtService.isTokenValid(jwt, userDetails) && Boolean.FALSE.equals(isTokenRevoked)) {
            var authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }

    private Boolean getIsTokenRevoked(String jwt) {
        return tokenRepository.findByToken(jwt)
                .map(TokenEntity::getRevoked)
                .orElse(false);
    }
}
