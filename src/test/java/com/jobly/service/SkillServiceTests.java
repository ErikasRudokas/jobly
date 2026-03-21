package com.jobly.service;

import com.jobly.dao.SkillDao;
import com.jobly.gen.model.SearchSkillsResponse;
import com.jobly.model.SkillEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.jobly.util.TestEntityFactory.buildSkill;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTests {

    @Mock
    private SkillDao skillDao;

    @InjectMocks
    private SkillService skillService;

    @Test
    void searchSkills_mapsResultsAndTotal() {
        String search = "java";
        Integer offset = 0;
        Integer limit = 5;

        SkillEntity first = buildSkill(1L, "Java", new Float[]{1.0F, 0.0F});
        SkillEntity second = buildSkill(2L, "JavaScript", new Float[]{0.9F, 0.1F});

        when(skillDao.searchSkills(search, offset, limit)).thenReturn(List.of(first, second));
        when(skillDao.countSkillsBySearch(search)).thenReturn(2);

        SearchSkillsResponse response = skillService.searchSkills(search, offset, limit);

        assertEquals(2, response.getTotal());
        assertNotNull(response.getSkills());
        assertEquals(2, response.getSkills().size());
        assertEquals(first.getId(), response.getSkills().get(0).getId());
        assertEquals(first.getName(), response.getSkills().get(0).getName());
    }

    @Test
    void searchSkills_returnsEmptyListWhenNoResults() {
        String search = "golang";

        when(skillDao.searchSkills(search, null, null)).thenReturn(List.of());
        when(skillDao.countSkillsBySearch(search)).thenReturn(0);

        SearchSkillsResponse response = skillService.searchSkills(search, null, null);

        assertEquals(0, response.getTotal());
        assertNotNull(response.getSkills());
        assertEquals(0, response.getSkills().size());
    }
}

