package com.jobly.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobly.util.ExceptionHandlerUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private static final String GENERIC_FORBIDDEN_MESSAGE = "Forbidden: Insufficient privileges to access this resource.";

    private static final String GENERIC_UNAUTHORIZED_MESSAGE = "Unauthorized: Missing or invalid credentials.";

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        var unauthorizedError = ExceptionHandlerUtils.getUnauthorizedError(GENERIC_UNAUTHORIZED_MESSAGE);
        writeExceptionToResponse(response, unauthorizedError, HttpStatus.UNAUTHORIZED);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        var forbiddenError = ExceptionHandlerUtils.getForbiddenError(GENERIC_FORBIDDEN_MESSAGE);
        writeExceptionToResponse(response, forbiddenError, HttpStatus.FORBIDDEN);
    }

    private void writeExceptionToResponse(HttpServletResponse response, Object error, HttpStatus status) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
