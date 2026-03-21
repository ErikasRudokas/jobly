package com.jobly.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobly.gen.api.AuthApiController;
import com.jobly.gen.model.*;
import com.jobly.security.filter.JwtAuthenticationFilter;
import com.jobly.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AuthApiController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@Import(AuthApiHandler.class)
@ImportAutoConfiguration(exclude = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
class AuthApiHandlerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Test
    void login_returnsTokens() throws Exception {
        UserLoginRequest request = new UserLoginRequest()
                .email("john@jobly.test")
                .password("secret123");
        UserLoginResponse response = new UserLoginResponse()
                .accessToken("access")
                .refreshToken("refresh");

        when(authenticationService.login(any(UserLoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access"))
                .andExpect(jsonPath("$.refreshToken").value("refresh"));

        ArgumentCaptor<UserLoginRequest> captor = ArgumentCaptor.forClass(UserLoginRequest.class);
        verify(authenticationService).login(captor.capture());
        assertEquals("john@jobly.test", captor.getValue().getEmail());
    }

    @Test
    void register_returnsCreatedWithLocation() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest()
                .firstName("Jane")
                .lastName("Roe")
                .username("jane")
                .email("jane@jobly.test")
                .password("secret123");
        UserRegisterResponse response = new UserRegisterResponse()
                .id(55L)
                .firstName("Jane")
                .lastName("Roe")
                .username("jane")
                .email("jane@jobly.test");

        when(authenticationService.register(any(UserRegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/user/55"))
                .andExpect(jsonPath("$.id").value(55))
                .andExpect(jsonPath("$.email").value("jane@jobly.test"));

        ArgumentCaptor<UserRegisterRequest> captor = ArgumentCaptor.forClass(UserRegisterRequest.class);
        verify(authenticationService).register(captor.capture());
        assertEquals("jane@jobly.test", captor.getValue().getEmail());
    }

    @Test
    void refreshToken_returnsAccessToken() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest().refreshToken("refresh");
        RefreshTokenResponse response = new RefreshTokenResponse().accessToken("new-access");

        when(authenticationService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access"));

        ArgumentCaptor<RefreshTokenRequest> captor = ArgumentCaptor.forClass(RefreshTokenRequest.class);
        verify(authenticationService).refreshToken(captor.capture());
        assertEquals("refresh", captor.getValue().getRefreshToken());
    }
}
