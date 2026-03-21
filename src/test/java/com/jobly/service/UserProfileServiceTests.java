package com.jobly.service;

import com.jobly.dao.*;
import com.jobly.enums.Role;
import com.jobly.exception.general.ForbiddenException;
import com.jobly.gen.model.*;
import com.jobly.model.*;
import com.jobly.service.validator.UserProfileValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static com.jobly.util.TestEntityFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTests {

    @Mock
    private UserSkillDao userSkillDao;

    @Mock
    private UserEducationDao userEducationDao;

    @Mock
    private UserWorkExperienceDao userWorkExperienceDao;

    @Mock
    private UserDao userDao;

    @Mock
    private SkillDao skillDao;

    @Mock
    private UserProfileValidator userProfileValidator;

    @InjectMocks
    private UserProfileService userProfileService;

    @Test
    void getUserProfile_mapsAllSections() {
        Long userId = 1L;
        UserEntity user = buildUser(userId, "John", "Doe", "john@jobly.test", "john");
        SkillEntity skill = buildSkill(10L, "Java", new Float[]{1.0F, 0.0F});

        UserSkillEntity userSkill = buildUserSkillWithStatus(100L, user, skill, SkillProficiency.INTERMEDIATE, CVDataStatus.USER_REVIEWED);
        UserEducationEntity education = buildUserEducation(200L, user, "Tech University", "CS", CVDataStatus.USER_REVIEWED);
        UserWorkExperienceEntity work = buildUserWorkExperience(300L, user, "Acme", "Developer", CVDataStatus.USER_REVIEWED);

        when(userSkillDao.findAllByUserId(userId)).thenReturn(List.of(userSkill));
        when(userEducationDao.findAllByUserId(userId)).thenReturn(List.of(education));
        when(userWorkExperienceDao.findAllByUserId(userId)).thenReturn(List.of(work));

        var response = userProfileService.getUserProfile(userId);

        assertEquals(1, response.getSkills().size());
        assertEquals(1, response.getEducation().size());
        assertEquals(1, response.getWorkExperience().size());
        UserSkill responseSkill = response.getSkills().get(0);
        assertEquals(userSkill.getId(), responseSkill.getId());
        UserEducation responseEducation = response.getEducation().get(0);
        assertEquals(education.getInstitutionName(), responseEducation.getInstitutionName());
        UserWorkExperience responseWork = response.getWorkExperience().get(0);
        assertEquals(work.getCompanyName(), responseWork.getCompanyName());
    }

    @Test
    void saveUserProfile_savesSkillsEducationAndWork() {
        Long userId = 2L;
        UserEntity user = buildUser(userId, "Jane", "Roe", "jane@jobly.test", "jane");

        UserSkillEntity existingSkill = buildUserSkillWithStatus(10L, user, buildSkill(1L, "Java", new Float[]{1.0F, 0.0F}), SkillProficiency.BEGINNER, CVDataStatus.AI_PARSED);
        UserSkillEntity deleteSkill = buildUserSkillWithStatus(11L, user, buildSkill(2L, "SQL", new Float[]{0.5F, 0.5F}), SkillProficiency.BEGINNER, CVDataStatus.AI_PARSED);

        UserEducationEntity existingEducation = buildUserEducation(20L, user, "Tech University", "CS", CVDataStatus.AI_PARSED);
        UserEducationEntity deleteEducation = buildUserEducation(21L, user, "Old School", "Math", CVDataStatus.AI_PARSED);

        UserWorkExperienceEntity existingWork = buildUserWorkExperience(30L, user, "Acme", "Developer", CVDataStatus.AI_PARSED);
        UserWorkExperienceEntity deleteWork = buildUserWorkExperience(31L, user, "Old Inc", "Intern", CVDataStatus.AI_PARSED);

        SaveUserSkillRequest updateSkillRequest = new SaveUserSkillRequest()
                .id(existingSkill.getId())
                .skillId(existingSkill.getSkill().getId())
                .proficiencyLevel(SkillProficiency.ADVANCED);
        SaveUserSkillRequest deleteSkillRequest = new SaveUserSkillRequest()
                .id(deleteSkill.getId())
                .delete(true);
        SaveUserSkillRequest newSkillRequest = new SaveUserSkillRequest()
                .skillId(3L)
                .proficiencyLevel(SkillProficiency.INTERMEDIATE);

        SaveUserEducationRequest updateEducationRequest = new SaveUserEducationRequest()
                .id(existingEducation.getId())
                .institutionName("Tech University")
                .degree("CS")
                .startDate(OffsetDateTime.parse("2020-01-01T00:00:00Z"));
        SaveUserEducationRequest deleteEducationRequest = new SaveUserEducationRequest()
                .id(deleteEducation.getId())
                .delete(true);
        SaveUserEducationRequest newEducationRequest = new SaveUserEducationRequest()
                .institutionName("New University")
                .degree("SE")
                .startDate(OffsetDateTime.parse("2021-01-01T00:00:00Z"));

        SaveUserWorkExperienceRequest updateWorkRequest = new SaveUserWorkExperienceRequest()
                .id(existingWork.getId())
                .companyName("Acme")
                .designation("Senior Dev")
                .startDate(OffsetDateTime.parse("2019-01-01T00:00:00Z"));
        SaveUserWorkExperienceRequest deleteWorkRequest = new SaveUserWorkExperienceRequest()
                .id(deleteWork.getId())
                .delete(true);
        SaveUserWorkExperienceRequest newWorkRequest = new SaveUserWorkExperienceRequest()
                .companyName("New Co")
                .designation("Engineer")
                .startDate(OffsetDateTime.parse("2022-01-01T00:00:00Z"));

        SaveUserProfileRequest request = new SaveUserProfileRequest()
                .skills(List.of(updateSkillRequest, deleteSkillRequest, newSkillRequest))
                .education(List.of(updateEducationRequest, deleteEducationRequest, newEducationRequest))
                .workExperience(List.of(updateWorkRequest, deleteWorkRequest, newWorkRequest));

        SkillEntity newSkill = buildSkill(3L, "Docker", new Float[]{0.2F, 0.8F});
        SkillEntity updatedSkill = existingSkill.getSkill();

        when(userDao.findById(userId)).thenReturn(user);
        when(userSkillDao.findAllByUserId(userId)).thenReturn(List.of(existingSkill, deleteSkill));
        when(userEducationDao.findAllByUserId(userId)).thenReturn(List.of(existingEducation, deleteEducation));
        when(userWorkExperienceDao.findAllByUserId(userId)).thenReturn(List.of(existingWork, deleteWork));
        when(skillDao.findAllByIds(List.of(updatedSkill.getId(), newSkill.getId()))).thenReturn(Set.of(updatedSkill, newSkill));
        when(userSkillDao.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userEducationDao.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userWorkExperienceDao.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = userProfileService.saveUserProfile(userId, request);

        verify(userProfileValidator).validateUserProfileSaveRequest(request);
        verify(userSkillDao).deleteAll(any());
        verify(userEducationDao).deleteAll(any());
        verify(userWorkExperienceDao).deleteAll(any());

        assertEquals(2, response.getSkills().size());
        assertEquals(2, response.getEducation().size());
        assertEquals(2, response.getWorkExperience().size());
    }

    @Test
    void saveUserProfile_throwsWhenSkillDoesNotBelongToUser() {
        Long userId = 3L;
        UserEntity user = buildUser(userId, "Alex", "North", "alex@jobly.test", "alex");

        SaveUserSkillRequest updateSkillRequest = new SaveUserSkillRequest()
                .id(99L)
                .skillId(1L)
                .proficiencyLevel(SkillProficiency.BEGINNER);

        SaveUserProfileRequest request = new SaveUserProfileRequest()
                .skills(List.of(updateSkillRequest))
                .education(List.of())
                .workExperience(List.of());

        when(userDao.findById(userId)).thenReturn(user);
        when(userSkillDao.findAllByUserId(userId)).thenReturn(List.of());

        assertThrows(ForbiddenException.class, () -> userProfileService.saveUserProfile(userId, request));
        verify(userSkillDao, never()).saveAll(any());
    }

    @Test
    void getUserProfileById_requiresUserRole() {
        Long userId = 4L;
        UserEntity user = buildUser(userId, "Sam", "Ray", "sam@jobly.test", "sam");
        user.setRole(Role.EMPLOYER);

        when(userDao.findById(userId)).thenReturn(user);

        assertThrows(ForbiddenException.class, () -> userProfileService.getUserProfileById(userId));
    }

    @Test
    void isUserProfileSetUp_returnsTrueWhenAnySectionExists() {
        Long userId = 5L;

        when(userEducationDao.existsByUserId(userId)).thenReturn(true);

        assertTrue(userProfileService.isUserProfileSetUp(userId));
    }

    @Test
    void isUserProfileReviewed_returnsFalseWhenAnySectionUnreviewed() {
        Long userId = 6L;
        UserEntity user = buildUser(userId, "Lee", "Poe", "lee@jobly.test", "lee");
        SkillEntity skill = buildSkill(10L, "Java", new Float[]{1.0F, 0.0F});

        UserSkillEntity aiSkill = buildUserSkillWithStatus(1L, user, skill, SkillProficiency.BEGINNER, CVDataStatus.AI_PARSED);
        UserEducationEntity reviewedEducation = buildUserEducation(2L, user, "Uni", "CS", CVDataStatus.USER_REVIEWED);
        UserWorkExperienceEntity reviewedWork = buildUserWorkExperience(3L, user, "Acme", "Dev", CVDataStatus.USER_REVIEWED);

        when(userSkillDao.findAllByUserId(userId)).thenReturn(List.of(aiSkill));
        when(userEducationDao.findAllByUserId(userId)).thenReturn(List.of(reviewedEducation));
        when(userWorkExperienceDao.findAllByUserId(userId)).thenReturn(List.of(reviewedWork));

        assertFalse(userProfileService.isUserProfileReviewed(userId));
    }

    @Test
    void isUserProfileReviewed_returnsTrueWhenAllReviewed() {
        Long userId = 7L;
        UserEntity user = buildUser(userId, "Mia", "Ray", "mia@jobly.test", "mia");
        SkillEntity skill = buildSkill(11L, "Java", new Float[]{1.0F, 0.0F});

        UserSkillEntity reviewedSkill = buildUserSkillWithStatus(1L, user, skill, SkillProficiency.BEGINNER, CVDataStatus.USER_REVIEWED);
        UserEducationEntity reviewedEducation = buildUserEducation(2L, user, "Uni", "CS", CVDataStatus.USER_REVIEWED);
        UserWorkExperienceEntity reviewedWork = buildUserWorkExperience(3L, user, "Acme", "Dev", CVDataStatus.USER_REVIEWED);

        when(userSkillDao.findAllByUserId(userId)).thenReturn(List.of(reviewedSkill));
        when(userEducationDao.findAllByUserId(userId)).thenReturn(List.of(reviewedEducation));
        when(userWorkExperienceDao.findAllByUserId(userId)).thenReturn(List.of(reviewedWork));

        assertTrue(userProfileService.isUserProfileReviewed(userId));
    }
}

