package com.jobly.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobly.gen.api.JobOffersApiController;
import com.jobly.gen.model.*;
import com.jobly.security.filter.JwtAuthenticationFilter;
import com.jobly.security.service.JwtService;
import com.jobly.service.JobOfferService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = JobOffersApiController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@Import(JobOfferApiHandler.class)
@ImportAutoConfiguration(exclude = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
class JobOfferApiHandlerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JobOfferService jobOfferService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void getAllJobOffers_returnsList() throws Exception {
        JobOfferWithSkillMatchListObject listObject = new JobOfferWithSkillMatchListObject();
        listObject.setId(1L);
        listObject.setTitle("Backend Developer");

        GetAllJobOffersResponse response = new GetAllJobOffersResponse()
                .total(1)
                .jobOffers(List.of(listObject));

        when(jwtService.extractUserIdOrNull(any(HttpServletRequest.class))).thenReturn(null);
        when(jobOfferService.findAll(any(), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/job-offers")
                        .param("search", "dev")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.jobOffers[0].id").value(1));
    }

    @Test
    void getJobOfferById_returnsDetails() throws Exception {
        Long jobOfferId = 10L;
        JobOfferDetailsResponse response = new JobOfferDetailsResponse();
        response.setJobOffer(new JobOffer().id(jobOfferId).title("Backend"));

        when(jobOfferService.findById(jobOfferId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/job-offers/{id}", jobOfferId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobOffer.id").value(jobOfferId))
                .andExpect(jsonPath("$.jobOffer.title").value("Backend"));
    }

    @Test
    void getOwnedJobOffers_returnsList() throws Exception {
        Long userId = 20L;
        GetMineJobOffersResponse response = new GetMineJobOffersResponse()
                .total(1)
                .jobOffers(List.of(new JobOfferListObject().id(2L).title("Backend")));

        when(jwtService.extractUserId(any(HttpServletRequest.class))).thenReturn(userId);
        when(jobOfferService.findJobOffersByUserId(anyLong(), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/job-offers/mine")
                        .param("search", "dev"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.jobOffers[0].id").value(2));
    }

    @Test
    void getOwnedJobOfferDetails_returnsDetails() throws Exception {
        Long userId = 21L;
        Long jobOfferId = 11L;
        JobOfferWithApplicationsResponse response = new JobOfferWithApplicationsResponse();
        response.setJobOffer(new JobOffer().id(jobOfferId).title("Backend"));
        response.setTotalApplications(3);

        when(jwtService.extractUserId(any(HttpServletRequest.class))).thenReturn(userId);
        when(jobOfferService.findOwnedJobOfferDetails(jobOfferId, userId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/job-offers/mine/{id}", jobOfferId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobOffer.id").value(jobOfferId))
                .andExpect(jsonPath("$.totalApplications").value(3));
    }

    @Test
    void getOwnedJobOfferApplications_returnsList() throws Exception {
        Long userId = 22L;
        Long jobOfferId = 12L;

        ApplicationWithSkillMatch application = new ApplicationWithSkillMatch();
        application.setId(100L);
        application.setCvId(9L);

        JobOfferApplicationsResponse response = new JobOfferApplicationsResponse();
        response.setTotalApplications(1);
        response.setApplications(List.of(application));

        when(jwtService.extractUserId(any(HttpServletRequest.class))).thenReturn(userId);
        when(jobOfferService.findOwnedJobOfferApplications(anyLong(), anyLong(), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/job-offers/mine/{id}/applications", jobOfferId)
                        .param("status", "PENDING")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalApplications").value(1))
                .andExpect(jsonPath("$.applications[0].id").value(100))
                .andExpect(jsonPath("$.applications[0].cvId").value(9));
    }

    @Test
    void createJobOffer_returnsCreated() throws Exception {
        Long userId = 23L;
        CreateJobOfferRequest request = new CreateJobOfferRequest()
                .title("Backend")
                .description("Desc")
                .companyName("Acme")
                .salary(1200.0F)
                .yearsOfExperience(3)
                .workType(WorkType.REMOTE)
                .contactEmail("hr@acme.test")
                .categoryId(1L);

        JobOffer response = new JobOffer().id(55L).title("Backend");

        when(jwtService.extractUserId(any(HttpServletRequest.class))).thenReturn(userId);
        when(jobOfferService.createJobOffer(any(CreateJobOfferRequest.class), anyLong())).thenReturn(response);

        mockMvc.perform(post("/api/v1/job-offers/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/job-offers/55"))
                .andExpect(jsonPath("$.id").value(55));
    }

    @Test
    void updateJobOffer_returnsUpdated() throws Exception {
        Long userId = 24L;
        Long jobOfferId = 56L;
        UpdateJobOfferRequest request = new UpdateJobOfferRequest()
                .title("Backend")
                .description("Desc")
                .companyName("Acme")
                .salary(1300.0F)
                .yearsOfExperience(4)
                .workType(WorkType.REMOTE)
                .contactEmail("hr@acme.test")
                .categoryId(1L);

        JobOffer response = new JobOffer().id(jobOfferId).title("Backend");

        when(jwtService.extractUserId(any(HttpServletRequest.class))).thenReturn(userId);
        when(jobOfferService.updateJobOffer(any(UpdateJobOfferRequest.class), anyLong(), anyLong())).thenReturn(response);

        mockMvc.perform(put("/api/v1/job-offers/{id}", jobOfferId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(jobOfferId));
    }

    @Test
    void deleteJobOffer_returnsNoContent() throws Exception {
        Long userId = 25L;
        Long jobOfferId = 57L;

        when(jwtService.extractUserId(any(HttpServletRequest.class))).thenReturn(userId);

        mockMvc.perform(delete("/api/v1/job-offers/{id}", jobOfferId))
                .andExpect(status().isNoContent());

        verify(jobOfferService).deleteJobOffer(jobOfferId, userId);
    }

    @Test
    void applyToJobOffer_returnsCreated() throws Exception {
        Long userId = 26L;
        Long jobOfferId = 58L;
        ApplicationCreateRequest request = new ApplicationCreateRequest().comment("Hello");
        Application response = new Application().id(88L).comment("Hello");

        when(jwtService.extractUserId(any(HttpServletRequest.class))).thenReturn(userId);
        when(jobOfferService.applyToJobOffer(jobOfferId, userId, request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/job-offers/{jobOfferId}/apply", jobOfferId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/applications/mine/88"))
                .andExpect(jsonPath("$.id").value(88));
    }

    @Test
    void canApplyToJobOffer_returnsResponse() throws Exception {
        Long userId = 27L;
        Long jobOfferId = 59L;
        CanApplyResponse response = new CanApplyResponse().canApply(true);

        when(jwtService.extractUserId(any(HttpServletRequest.class))).thenReturn(userId);
        when(jobOfferService.canApplyToJobOffer(jobOfferId, userId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/job-offers/{jobOfferId}/can-apply", jobOfferId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canApply").value(true));
    }
}

