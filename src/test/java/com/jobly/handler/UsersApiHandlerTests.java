package com.jobly.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobly.gen.api.UsersApiController;
import com.jobly.gen.model.*;
import com.jobly.security.filter.JwtAuthenticationFilter;
import com.jobly.security.service.JwtService;
import com.jobly.service.CvService;
import com.jobly.service.UserProfileService;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UsersApiController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@Import(UsersApiHandler.class)
@ImportAutoConfiguration(exclude = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
class UsersApiHandlerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CvService cvService;

    @MockitoBean
    private UserProfileService userProfileService;

    @Test
    void getUserDetails_returnsDetails() throws Exception {
        Long userId = 10L;
        GetUserDetailsResponse response = new GetUserDetailsResponse()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john@jobly.test")
                .username("john")
                .cvId(5L);

        when(jwtService.extractUserId(any(HttpServletRequest.class))).thenReturn(userId);
        when(cvService.getUserDetailsWithCv(userId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.cvId").value(5));
    }

    @Test
    void uploadUserCv_returnsResponse() throws Exception {
        Long userId = 11L;
        CvUploadResponse response = new CvUploadResponse().cvId(99L);

        when(jwtService.extractUserId(any(HttpServletRequest.class))).thenReturn(userId);
        when(cvService.uploadUserCv(eq(userId), any(MultipartFile.class))).thenReturn(response);

        mockMvc.perform(multipart("/api/v1/users/cv")
                        .file("file", "cv".getBytes()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cvId").value(99));
    }

    @Test
    void downloadUserCv_returnsResource() throws Exception {
        Long userId = 12L;
        Long cvId = 77L;
        byte[] bytes = "pdf".getBytes();
        Resource resource = new ByteArrayResource(bytes);

        ResponseEntity<Resource> response = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cv.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);

        when(jwtService.extractUserId(any(HttpServletRequest.class))).thenReturn(userId);
        when(cvService.downloadUserCv(cvId, userId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/cv/{id}", cvId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cv.pdf"));
    }

    @Test
    void getUserProfile_returnsProfile() throws Exception {
        Long userId = 13L;
        GetUserProfileResponse response = new GetUserProfileResponse()
                .skills(List.of())
                .education(List.of())
                .workExperience(List.of());

        when(jwtService.extractUserId(any(HttpServletRequest.class))).thenReturn(userId);
        when(userProfileService.getUserProfile(userId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skills").isArray());
    }

    @Test
    void saveUserProfile_returnsProfile() throws Exception {
        Long userId = 14L;
        SaveUserProfileRequest request = new SaveUserProfileRequest()
                .skills(List.of())
                .education(List.of())
                .workExperience(List.of());
        GetUserProfileResponse response = new GetUserProfileResponse()
                .skills(List.of())
                .education(List.of())
                .workExperience(List.of());

        when(jwtService.extractUserId(any(HttpServletRequest.class))).thenReturn(userId);
        when(userProfileService.saveUserProfile(eq(userId), any(SaveUserProfileRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workExperience").isArray());
    }

    @Test
    void getUserProfileById_returnsProfile() throws Exception {
        Long userId = 15L;
        GetUserProfileByIdResponse response = new GetUserProfileByIdResponse()
                .skills(List.of())
                .education(List.of())
                .workExperience(List.of());

        when(userProfileService.getUserProfileById(userId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/{userId}/profile", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.education").isArray());
    }
}

