package com.jobly.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobly.gen.api.ApplicationsApiController;
import com.jobly.gen.model.*;
import com.jobly.security.filter.JwtAuthenticationFilter;
import com.jobly.security.service.JwtService;
import com.jobly.service.ApplicationService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ApplicationsApiController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@Import(ApplicationApiHandler.class)
@ImportAutoConfiguration(exclude = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
class ApplicationApiHandlerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private ApplicationService applicationService;

    @Test
    void cancelApplication_returnsNoContent() throws Exception {
        Long applicationId = 10L;
        Long userId = 20L;

        when(jwtService.extractUserId(any(HttpServletRequest.class))).thenReturn(userId);

        mockMvc.perform(post("/api/v1/applications/{id}/cancel", applicationId))
                .andExpect(status().isNoContent());

        verify(applicationService).cancelApplication(applicationId, userId);
    }

    @Test
    void getMyApplication_returnsApplication() throws Exception {
        Long applicationId = 11L;
        Long userId = 21L;
        MyApplication response = new MyApplication()
                .id(applicationId)
                .comment("Notes")
                .applicationStatus(ApplicationStatus.PENDING)
                .cvId(5L)
                .jobOffer(buildJobOfferListObject());

        when(jwtService.extractUserId(any(HttpServletRequest.class))).thenReturn(userId);
        when(applicationService.getMyApplication(applicationId, userId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/applications/mine/{id}", applicationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(applicationId))
                .andExpect(jsonPath("$.comment").value("Notes"))
                .andExpect(jsonPath("$.cvId").value(5))
                .andExpect(jsonPath("$.jobOffer.id").value(1));
    }

    @Test
    void getMyApplications_returnsList() throws Exception {
        Long userId = 22L;

        MyApplicationListObject listObject = new MyApplicationListObject();
        listObject.setId(30L);
        listObject.setApplicationStatus(ApplicationStatus.REJECTED);
        listObject.setJobOffer(buildJobOfferListObject());

        GetMyApplicationsResponse response = new GetMyApplicationsResponse()
                .total(1)
                .applications(List.of(listObject));

        when(jwtService.extractUserId(any(HttpServletRequest.class))).thenReturn(userId);
        when(applicationService.getMyApplications(any(Long.class), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/applications/mine")
                        .param("status", "REJECTED")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.applications[0].id").value(30))
                .andExpect(jsonPath("$.applications[0].jobOffer.id").value(1));
    }

    @Test
    void manageApplication_returnsNoContent() throws Exception {
        Long applicationId = 12L;
        Long userId = 23L;
        ApplicationManageRequest request = new ApplicationManageRequest().action(ApplicationManageAction.APPROVE);

        when(jwtService.extractUserId(any(HttpServletRequest.class))).thenReturn(userId);

        mockMvc.perform(post("/api/v1/applications/{id}/manage", applicationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(applicationService).manageApplication(applicationId, userId, request);
    }

    @Test
    void updateApplication_returnsApplication() throws Exception {
        Long applicationId = 13L;
        Long userId = 24L;
        ApplicationUpdateRequest request = new ApplicationUpdateRequest().comment("Updated");

        Application response = new Application()
                .id(applicationId)
                .comment("Updated")
                .applicationStatus(ApplicationStatus.PENDING)
                .cvId(6L);

        when(jwtService.extractUserId(any(HttpServletRequest.class))).thenReturn(userId);
        when(applicationService.updateApplication(applicationId, userId, request)).thenReturn(response);

        mockMvc.perform(put("/api/v1/applications/{id}", applicationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(applicationId))
                .andExpect(jsonPath("$.comment").value("Updated"))
                .andExpect(jsonPath("$.cvId").value(6));
    }

    private static JobOfferListObject buildJobOfferListObject() {
        JobOfferCategory category = new JobOfferCategory();
        category.setId(2L);
        category.setName("Engineering");

        JobOfferListObject jobOffer = new JobOfferListObject();
        jobOffer.setId(1L);
        jobOffer.setTitle("Backend Developer");
        jobOffer.setCompanyName("Acme");
        jobOffer.setSalary(1000.0F);
        jobOffer.setWorkType(WorkType.REMOTE);
        jobOffer.setLocation("Vilnius");
        jobOffer.setCategory(category);
        return jobOffer;
    }
}
