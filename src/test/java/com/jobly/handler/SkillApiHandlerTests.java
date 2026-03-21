package com.jobly.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobly.gen.api.SkillsApiController;
import com.jobly.gen.model.SearchSkillsResponse;
import com.jobly.gen.model.SkillDetails;
import com.jobly.security.filter.JwtAuthenticationFilter;
import com.jobly.service.SkillService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = SkillsApiController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@Import(SkillHandler.class)
@ImportAutoConfiguration(exclude = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
class SkillApiHandlerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SkillService skillService;

    @Test
    void searchSkills_returnsResults() throws Exception {
        SearchSkillsResponse response = new SearchSkillsResponse()
                .total(1)
                .skills(List.of(new SkillDetails().id(1L).name("Java")));

        when(skillService.searchSkills("java", 0, 10)).thenReturn(response);

        mockMvc.perform(get("/api/v1/skills")
                        .param("value", "java")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.skills[0].id").value(1))
                .andExpect(jsonPath("$.skills[0].name").value("Java"));

        verify(skillService).searchSkills("java", 0, 10);
    }
}

