package com.jobly.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobly.gen.api.AdminApiController;
import com.jobly.gen.model.*;
import com.jobly.security.filter.JwtAuthenticationFilter;
import com.jobly.security.service.JwtService;
import com.jobly.service.AdminUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AdminApiController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@Import(AdminApiHandler.class)
@ImportAutoConfiguration(exclude = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
class AdminApiHandlerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AdminUserService adminUserService;

    @Test
    void getSystemUsers_returnsList() throws Exception {
        AdminUserListItem item = new AdminUserListItem()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .username("john")
                .email("john@jobly.test")
                .role(AdminUserRole.USER)
                .suspended(false);

        AdminUserListResponse response = new AdminUserListResponse()
                .total(1)
                .users(List.of(item));

        when(adminUserService.getSystemUsers(eq("john"), eq(0), eq(10))).thenReturn(response);

        mockMvc.perform(get("/api/v1/admin/users")
                        .queryParam("search", "john")
                        .queryParam("offset", "0")
                        .queryParam("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.users[0].username").value("john"));
    }

    @Test
    void getAdminUserDetails_returnsDetails() throws Exception {
        AdminUserDetails user = new AdminUserDetails()
                .id(2L)
                .firstName("Ana")
                .lastName("Vale")
                .username("ana")
                .email("ana@jobly.test")
                .role(AdminUserRole.EMPLOYER)
                .suspended(true);

        AdminUserActionPerformedBy performedBy = new AdminUserActionPerformedBy()
                .id(9L)
                .firstName("Alice")
                .lastName("Admin")
                .username("alice");

        AdminUserAction action = new AdminUserAction()
                .id(100L)
                .action(AdminUserActionType.SUSPEND)
                .comment("Policy violation")
                .performedBy(performedBy);

        AdminUserDetailsResponse response = new AdminUserDetailsResponse()
                .user(user)
                .actions(List.of(action));

        when(adminUserService.getAdminUserDetails(2L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/admin/users/{userId}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value("ana"))
                .andExpect(jsonPath("$.actions[0].action").value("SUSPEND"));
    }

    @Test
    void manageAdminUserStatus_returnsNoContent() throws Exception {
        Long adminUserId = 10L;
        AdminUserStatusManageRequest request = new AdminUserStatusManageRequest()
                .action(AdminUserActionType.RESTORE)
                .comment("Reinstated after review");

        when(jwtService.extractUserId(any(HttpServletRequest.class))).thenReturn(adminUserId);

        mockMvc.perform(post("/api/v1/admin/users/{userId}/status", 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }
}

