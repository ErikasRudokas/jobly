package com.jobly.service.handler;

import com.jobly.dao.*;
import com.jobly.gen.api.parser.data.CvParseResponse;
import com.jobly.gen.api.parser.data.Education;
import com.jobly.gen.api.parser.data.WorkExperience;
import com.jobly.gen.model.CVDataStatus;
import com.jobly.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static com.jobly.util.TestEntityFactory.buildSkill;
import static com.jobly.util.TestEntityFactory.buildUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CvParseHandlerTests {

    @Mock
    private CvDao cvDao;

    @Mock
    private UserWorkExperienceDao userWorkExperienceDao;

    @Mock
    private UserEducationDao userEducationDao;

    @Mock
    private UserSkillDao userSkillDao;

    @Mock
    private SkillDao skillDao;

    @InjectMocks
    private CvParseHandler cvParseHandler;

    @Test
    void saveParsedCvData_skipsWhenCvExists() {
        UserEntity user = buildUser(1L, "John", "Doe", "john@jobly.test", "john");
        UserCvEntity userCv = UserCvEntity.builder().user(user).build();
        CvParseResponse response = new CvParseResponse();

        when(cvDao.existsByUserId(user.getId())).thenReturn(true);

        cvParseHandler.saveParsedCvData(userCv, response);

        verify(userWorkExperienceDao, never()).saveAll(anyList());
        verify(userEducationDao, never()).saveAll(anyList());
        verify(userSkillDao, never()).saveAll(anyList());
        verify(skillDao, never()).findAllSkillAliases();
    }

    @Test
    void saveParsedCvData_persistsAllWhenNoExistingData() {
        UserEntity user = buildUser(2L, "Jane", "Roe", "jane@jobly.test", "jane");
        UserCvEntity userCv = UserCvEntity.builder().user(user).build();

        CvParseResponse response = new CvParseResponse()
                .workExperience(List.of(new WorkExperience().company("Acme").designation("Engineer")))
                .education(List.of(new Education().institution("Tech University").degree("CS")))
                .skills(List.of("Experienced Java developer"));

        SkillEntity skill = buildSkill(10L, "Java", new Float[]{1.0F, 0.0F});
        SkillAliasEntity alias = SkillAliasEntity.builder().alias("java").skill(skill).build();

        when(cvDao.existsByUserId(user.getId())).thenReturn(false);
        when(userWorkExperienceDao.existsByUserId(user.getId())).thenReturn(false);
        when(userEducationDao.existsByUserId(user.getId())).thenReturn(false);
        when(userSkillDao.existsByUserId(user.getId())).thenReturn(false);
        when(skillDao.findAllSkillAliases()).thenReturn(Map.of("java", alias));

        cvParseHandler.saveParsedCvData(userCv, response);

        ArgumentCaptor<List<UserWorkExperienceEntity>> workCaptor = ArgumentCaptor.forClass(List.class);
        verify(userWorkExperienceDao).saveAll(workCaptor.capture());
        assertEquals(1, workCaptor.getValue().size());
        assertEquals("Acme", workCaptor.getValue().get(0).getCompanyName());
        assertEquals(CVDataStatus.AI_PARSED, workCaptor.getValue().get(0).getStatus());

        ArgumentCaptor<List<UserEducationEntity>> educationCaptor = ArgumentCaptor.forClass(List.class);
        verify(userEducationDao).saveAll(educationCaptor.capture());
        assertEquals(1, educationCaptor.getValue().size());
        assertEquals("Tech University", educationCaptor.getValue().get(0).getInstitutionName());
        assertEquals(CVDataStatus.AI_PARSED, educationCaptor.getValue().get(0).getStatus());

        ArgumentCaptor<List<UserSkillEntity>> skillCaptor = ArgumentCaptor.forClass(List.class);
        verify(userSkillDao).saveAll(skillCaptor.capture());
        assertEquals(1, skillCaptor.getValue().size());
        assertEquals(skill.getId(), skillCaptor.getValue().get(0).getSkill().getId());
        assertEquals(CVDataStatus.AI_PARSED, skillCaptor.getValue().get(0).getStatus());
    }

    @Test
    void saveParsedCvData_skipsWorkExperienceWhenExists() {
        UserEntity user = buildUser(3L, "Alex", "North", "alex@jobly.test", "alex");
        UserCvEntity userCv = UserCvEntity.builder().user(user).build();

        CvParseResponse response = new CvParseResponse()
                .workExperience(List.of(new WorkExperience().company("Acme").designation("Engineer")))
                .education(List.of(new Education().institution("Tech University").degree("CS")))
                .skills(List.of("Java"));

        SkillEntity skill = buildSkill(11L, "Java", new Float[]{1.0F, 0.0F});
        SkillAliasEntity alias = SkillAliasEntity.builder().alias("java").skill(skill).build();

        when(cvDao.existsByUserId(user.getId())).thenReturn(false);
        when(userWorkExperienceDao.existsByUserId(user.getId())).thenReturn(true);
        when(userEducationDao.existsByUserId(user.getId())).thenReturn(false);
        when(userSkillDao.existsByUserId(user.getId())).thenReturn(false);
        when(skillDao.findAllSkillAliases()).thenReturn(Map.of("java", alias));

        cvParseHandler.saveParsedCvData(userCv, response);

        verify(userWorkExperienceDao, never()).saveAll(anyList());
        verify(userEducationDao).saveAll(anyList());
        verify(userSkillDao).saveAll(anyList());
    }

    @Test
    void saveParsedCvData_skipsEducationWhenExists() {
        UserEntity user = buildUser(4L, "Sam", "Ray", "sam@jobly.test", "sam");
        UserCvEntity userCv = UserCvEntity.builder().user(user).build();

        CvParseResponse response = new CvParseResponse()
                .workExperience(List.of(new WorkExperience().company("Acme").designation("Engineer")))
                .education(List.of(new Education().institution("Tech University").degree("CS")))
                .skills(List.of("Java"));

        SkillEntity skill = buildSkill(12L, "Java", new Float[]{1.0F, 0.0F});
        SkillAliasEntity alias = SkillAliasEntity.builder().alias("java").skill(skill).build();

        when(cvDao.existsByUserId(user.getId())).thenReturn(false);
        when(userWorkExperienceDao.existsByUserId(user.getId())).thenReturn(false);
        when(userEducationDao.existsByUserId(user.getId())).thenReturn(true);
        when(userSkillDao.existsByUserId(user.getId())).thenReturn(false);
        when(skillDao.findAllSkillAliases()).thenReturn(Map.of("java", alias));

        cvParseHandler.saveParsedCvData(userCv, response);

        verify(userEducationDao, never()).saveAll(anyList());
        verify(userWorkExperienceDao).saveAll(anyList());
        verify(userSkillDao).saveAll(anyList());
    }

    @Test
    void saveParsedCvData_skipsSkillsWhenExists() {
        UserEntity user = buildUser(5L, "Mia", "Ray", "mia@jobly.test", "mia");
        UserCvEntity userCv = UserCvEntity.builder().user(user).build();

        CvParseResponse response = new CvParseResponse()
                .workExperience(List.of(new WorkExperience().company("Acme").designation("Engineer")))
                .education(List.of(new Education().institution("Tech University").degree("CS")))
                .skills(List.of("Java"));

        when(cvDao.existsByUserId(user.getId())).thenReturn(false);
        when(userWorkExperienceDao.existsByUserId(user.getId())).thenReturn(false);
        when(userEducationDao.existsByUserId(user.getId())).thenReturn(false);
        when(userSkillDao.existsByUserId(user.getId())).thenReturn(true);

        cvParseHandler.saveParsedCvData(userCv, response);

        verify(skillDao, never()).findAllSkillAliases();
        verify(userSkillDao, never()).saveAll(anyList());
        verify(userEducationDao).saveAll(anyList());
        verify(userWorkExperienceDao).saveAll(anyList());
    }
}

