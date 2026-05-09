package com.jobly.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobly.gen.api.SkillsApiController;
import com.jobly.gen.model.*;
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
        controllers = SkillsApiController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@Import(SkillApiHandler.class)
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

        when(skillService.searchSkills("java", null, 0, 10)).thenReturn(response);

        mockMvc.perform(get("/api/v1/skills")
                        .param("value", "java")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.skills[0].id").value(1))
                .andExpect(jsonPath("$.skills[0].name").value("Java"));

        verify(skillService).searchSkills("java", null, 0, 10);
    }

    @Test
    void createSkill_returnsSkill() throws Exception {
        SkillCreateRequest request = new SkillCreateRequest()
                .name("Java")
                .description("Java skill")
                .type(SkillType.TECHNICAL)
                .aliases(List.of(new SkillAliasCreateRequest().value("JVM")));
        Skill response = new Skill()
                .id(11L)
                .name("Java")
                .type(SkillType.TECHNICAL)
                .aliases(List.of(new SkillAliasGetResponse().id(20L).value("JVM")));

        when(skillService.createSkill(any(SkillCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/skills/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.name").value("Java"))
                .andExpect(jsonPath("$.aliases[0].value").value("JVM"));

        verify(skillService).createSkill(any(SkillCreateRequest.class));
    }

    @Test
    void getSkillById_returnsSkill() throws Exception {
        Skill response = new Skill()
                .id(12L)
                .name("Kotlin")
                .type(SkillType.TECHNICAL)
                .aliases(List.of(new SkillAliasGetResponse().id(30L).value("JVM")));

        when(skillService.getSkillById(12L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/skills/{id}", 12L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.aliases[0].id").value(30));

        verify(skillService).getSkillById(12L);
    }

    @Test
    void updateSkill_returnsSkill() throws Exception {
        SkillUpdateRequest request = new SkillUpdateRequest()
                .name("Java 17")
                .description("Updated description")
                .type(SkillType.TECHNICAL)
                .aliases(List.of(new SkillAliasUpdateRequest().id(41L).value("JDK")));
        Skill response = new Skill()
                .id(14L)
                .name("Java 17")
                .type(SkillType.TECHNICAL)
                .aliases(List.of(new SkillAliasGetResponse().id(41L).value("JDK")));

        when(skillService.updateSkill(14L, request)).thenReturn(response);

        mockMvc.perform(put("/api/v1/skills/{id}", 14L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(14))
                .andExpect(jsonPath("$.name").value("Java 17"))
                .andExpect(jsonPath("$.aliases[0].value").value("JDK"));

        verify(skillService).updateSkill(14L, request);
    }

    @Test
    void deleteSkill_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/skills/{id}", 15L))
                .andExpect(status().isNoContent());

        verify(skillService).deleteSkillById(15L);
    }
}
