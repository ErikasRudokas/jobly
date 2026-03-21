package com.jobly.service;

import com.jobly.dao.*;
import com.jobly.dto.ApplicationFilterWrapper;
import com.jobly.dto.PaginationAndFilterWrapper;
import com.jobly.exception.general.BadRequestException;
import com.jobly.exception.general.ForbiddenException;
import com.jobly.gen.model.*;
import com.jobly.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.jobly.util.TestEntityFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobOfferServiceTests {

    @Mock
    private JobOfferDao jobOfferDao;

    @Mock
    private UserService userService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ApplicationDao applicationDao;

    @Mock
    private JobSkillDao jobSkillDao;

    @Mock
    private SkillDao skillDao;

    @Mock
    private CvDao cvDao;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private UserSkillDao userSkillDao;

    @InjectMocks
    private JobOfferService jobOfferService;

    @Test
    void findAll_withoutUserId_returnsNullSkillMatch() {
        PaginationAndFilterWrapper wrapper = PaginationAndFilterWrapper.builder()
                .search("dev")
                .offset(0)
                .limit(10)
                .build();
        JobOfferEntity jobOffer = buildJobOffer(1L, 10L);

        when(jobOfferDao.findAllWithPaginationAndFilter(wrapper)).thenReturn(List.of(jobOffer));
        when(jobOfferDao.countAllWithFilter(wrapper.getSearch())).thenReturn(1);
        when(jobSkillDao.findAllByJobOfferId(jobOffer.getId())).thenReturn(List.of());

        GetAllJobOffersResponse response = jobOfferService.findAll(null, wrapper);

        assertEquals(1, response.getTotal());
        assertEquals(1, response.getJobOffers().size());
        assertNull(response.getJobOffers().get(0).getUserSkillsMatch());
        verify(userSkillDao, never()).findAllByUserId(any());
    }

    @Test
    void findAll_withUserId_returnsSkillMatch() {
        Long userId = 25L;
        PaginationAndFilterWrapper wrapper = PaginationAndFilterWrapper.builder()
                .search("dev")
                .offset(0)
                .limit(10)
                .build();
        JobOfferEntity jobOffer = buildJobOffer(2L, 11L);

        SkillEntity skill = buildSkill(10L, "Java", new Float[]{1.0F, 0.0F});
        JobSkillEntity jobSkill = buildJobSkill(20L, jobOffer, skill, SkillProficiency.INTERMEDIATE);
        UserEntity user = buildUser(userId, "Sam", "Lee", "sam@jobly.test", "sam");
        UserSkillEntity userSkill = buildUserSkill(30L, user, skill, SkillProficiency.INTERMEDIATE);

        when(userSkillDao.findAllByUserId(userId)).thenReturn(List.of(userSkill));
        when(jobOfferDao.findAllWithPaginationAndFilter(wrapper)).thenReturn(List.of(jobOffer));
        when(jobOfferDao.countAllWithFilter(wrapper.getSearch())).thenReturn(1);
        when(jobSkillDao.findAllByJobOfferId(jobOffer.getId())).thenReturn(List.of(jobSkill));

        GetAllJobOffersResponse response = jobOfferService.findAll(userId, wrapper);

        assertEquals(1, response.getTotal());
        assertNotNull(response.getJobOffers().get(0).getUserSkillsMatch());
    }

    @Test
    void findById_returnsJobOfferDetails() {
        Long jobOfferId = 3L;
        JobOfferEntity jobOffer = buildJobOffer(jobOfferId, 12L);
        jobOffer.setSalary(BigDecimal.valueOf(1200));
        SkillEntity skill = buildSkill(11L, "SQL", new Float[]{0.5F, 0.5F});
        JobSkillEntity jobSkill = buildJobSkill(21L, jobOffer, skill, SkillProficiency.BEGINNER);

        when(jobOfferDao.findById(jobOfferId)).thenReturn(jobOffer);
        when(jobSkillDao.findAllByJobOfferId(jobOfferId)).thenReturn(List.of(jobSkill));

        JobOfferDetailsResponse response = jobOfferService.findById(jobOfferId);

        assertEquals(jobOfferId, response.getJobOffer().getId());
        assertEquals(1, response.getJobOffer().getSkills().size());
    }

    @Test
    void findJobOffersByUserId_returnsMine() {
        Long userId = 31L;
        PaginationAndFilterWrapper wrapper = PaginationAndFilterWrapper.builder()
                .search("dev")
                .offset(0)
                .limit(10)
                .build();
        JobOfferEntity jobOffer = buildJobOffer(4L, userId);

        when(jobOfferDao.findByUserIdWithFilter(userId, wrapper)).thenReturn(List.of(jobOffer));
        when(jobOfferDao.countByUserIdWithFilter(userId, wrapper.getSearch())).thenReturn(1);

        GetMineJobOffersResponse response = jobOfferService.findJobOffersByUserId(userId, wrapper);

        assertEquals(1, response.getTotal());
        assertEquals(1, response.getJobOffers().size());
        assertEquals(jobOffer.getId(), response.getJobOffers().get(0).getId());
    }

    @Test
    void findOwnedJobOfferDetails_ownerReturnsCounts() {
        Long userId = 32L;
        Long jobOfferId = 5L;
        JobOfferEntity jobOffer = buildJobOffer(jobOfferId, userId);
        SkillEntity skill = buildSkill(12L, "Java", new Float[]{1.0F, 0.0F});
        JobSkillEntity jobSkill = buildJobSkill(22L, jobOffer, skill, SkillProficiency.ADVANCED);

        when(jobOfferDao.findById(jobOfferId)).thenReturn(jobOffer);
        when(applicationDao.getCountOfAllPendingByJobOfferId(jobOfferId)).thenReturn(2);
        when(applicationDao.getCountOfAllRelevantApplicationsByJobOfferId(jobOfferId)).thenReturn(3);
        when(jobSkillDao.findAllByJobOfferId(jobOfferId)).thenReturn(List.of(jobSkill));

        JobOfferWithApplicationsResponse response = jobOfferService.findOwnedJobOfferDetails(jobOfferId, userId);

        assertEquals(3, response.getTotalApplications());
        assertEquals(2, response.getUnprocessedApplications());
        assertEquals(jobOfferId, response.getJobOffer().getId());
    }

    @Test
    void findOwnedJobOfferDetails_nonOwnerThrows() {
        Long jobOfferId = 6L;
        JobOfferEntity jobOffer = buildJobOffer(jobOfferId, 999L);

        when(jobOfferDao.findById(jobOfferId)).thenReturn(jobOffer);

        assertThrows(ForbiddenException.class, () -> jobOfferService.findOwnedJobOfferDetails(jobOfferId, 33L));
    }

    @Test
    void findOwnedJobOfferApplications_ownerReturnsApplications() {
        Long userId = 34L;
        Long jobOfferId = 7L;
        JobOfferEntity jobOffer = buildJobOffer(jobOfferId, userId);
        UserEntity applicant = buildUser(200L, "Alex", "North", "alex@jobly.test", "alex");
        ApplicationEntity application = buildApplication(100L, ApplicationStatus.PENDING, jobOffer);
        application.setApplicant(applicant);

        SkillEntity skill = buildSkill(13L, "Java", new Float[]{1.0F, 0.0F});
        JobSkillEntity jobSkill = buildJobSkill(23L, jobOffer, skill, SkillProficiency.INTERMEDIATE);
        UserSkillEntity userSkill = buildUserSkill(33L, applicant, skill, SkillProficiency.INTERMEDIATE);
        UserCvEntity userCv = buildUserCv(44L, applicant, "cv.pdf", new byte[]{1, 2});

        ApplicationFilterWrapper filterWrapper = ApplicationFilterWrapper.builder().build();

        when(jobOfferDao.findById(jobOfferId)).thenReturn(jobOffer);
        when(applicationDao.countAllByJobOfferId(jobOfferId, filterWrapper)).thenReturn(1);
        when(applicationDao.findAllByJobOfferId(jobOfferId, filterWrapper)).thenReturn(List.of(application));
        when(jobSkillDao.findAllByJobOfferId(jobOfferId)).thenReturn(List.of(jobSkill));
        when(cvDao.findMostRecentCv(applicant.getId())).thenReturn(userCv);
        when(userSkillDao.findAllByUserId(applicant.getId())).thenReturn(List.of(userSkill));

        JobOfferApplicationsResponse response = jobOfferService.findOwnedJobOfferApplications(jobOfferId, userId, filterWrapper);

        assertEquals(1, response.getTotalApplications());
        assertEquals(1, response.getApplications().size());
        assertEquals(userCv.getId(), response.getApplications().get(0).getCvId());
    }

    @Test
    void findOwnedJobOfferApplications_nonOwnerThrows() {
        Long jobOfferId = 8L;
        JobOfferEntity jobOffer = buildJobOffer(jobOfferId, 300L);

        when(jobOfferDao.findById(jobOfferId)).thenReturn(jobOffer);

        assertThrows(ForbiddenException.class, () -> jobOfferService.findOwnedJobOfferApplications(jobOfferId, 35L, ApplicationFilterWrapper.builder().build()));
    }

    @Test
    void createJobOffer_persistsSkillsAndReturnsJobOffer() {
        Long userId = 36L;
        UserEntity creator = buildUser(userId, "Chris", "Moss", "chris@jobly.test", "chris");
        CategoryEntity category = buildCategory(77L, "Engineering");

        CreateJobOfferSkill skillRequest = new CreateJobOfferSkill()
                .skillId(10L)
                .proficiency(SkillProficiency.INTERMEDIATE);

        CreateJobOfferRequest request = new CreateJobOfferRequest()
                .title("Backend")
                .description("Desc")
                .companyName("Acme")
                .salary(1500.0F)
                .yearsOfExperience(3)
                .workType(WorkType.REMOTE)
                .contactEmail("hr@acme.test")
                .categoryId(category.getId())
                .skills(List.of(skillRequest));

        JobOfferEntity savedJobOffer = buildJobOffer(9L, userId);
        savedJobOffer.setCompany("Acme");
        savedJobOffer.setSalary(BigDecimal.valueOf(1500));

        SkillEntity skill = buildSkill(10L, "Java", new Float[]{1.0F, 0.0F});
        JobSkillEntity jobSkill = buildJobSkill(50L, savedJobOffer, skill, SkillProficiency.INTERMEDIATE);

        when(userService.findById(userId)).thenReturn(creator);
        when(categoryService.findEntityById(category.getId())).thenReturn(category);
        when(jobOfferDao.save(any(JobOfferEntity.class))).thenReturn(savedJobOffer);
        when(skillDao.findAllByIds(List.of(10L))).thenReturn(Set.of(skill));
        when(jobSkillDao.saveAll(any())).thenReturn(List.of(jobSkill));

        JobOffer response = jobOfferService.createJobOffer(request, userId);

        assertEquals(savedJobOffer.getId(), response.getId());
        assertEquals(1, response.getSkills().size());
    }

    @Test
    void createJobOffer_missingSkillThrowsBadRequest() {
        Long userId = 37L;
        UserEntity creator = buildUser(userId, "Chris", "Moss", "chris@jobly.test", "chris");
        CategoryEntity category = buildCategory(78L, "Engineering");

        CreateJobOfferSkill skillRequest = new CreateJobOfferSkill()
                .skillId(111L)
                .proficiency(SkillProficiency.INTERMEDIATE);

        CreateJobOfferRequest request = new CreateJobOfferRequest()
                .title("Backend")
                .description("Desc")
                .companyName("Acme")
                .salary(1500.0F)
                .yearsOfExperience(3)
                .workType(WorkType.REMOTE)
                .contactEmail("hr@acme.test")
                .categoryId(category.getId())
                .skills(List.of(skillRequest));

        when(userService.findById(userId)).thenReturn(creator);
        when(categoryService.findEntityById(category.getId())).thenReturn(category);
        when(jobOfferDao.save(any(JobOfferEntity.class))).thenReturn(buildJobOffer(10L, userId));
        when(skillDao.findAllByIds(List.of(111L))).thenReturn(Set.of());

        assertThrows(BadRequestException.class, () -> jobOfferService.createJobOffer(request, userId));
        verify(jobSkillDao, never()).saveAll(any());
    }

    @Test
    void updateJobOffer_nonOwnerThrows() {
        Long userId = 38L;
        Long jobOfferId = 11L;
        JobOfferEntity jobOffer = buildJobOffer(jobOfferId, 999L);

        when(jobOfferDao.findById(jobOfferId)).thenReturn(jobOffer);

        UpdateJobOfferRequest request = new UpdateJobOfferRequest()
                .title("Backend")
                .description("Desc")
                .companyName("Acme")
                .salary(1500.0F)
                .yearsOfExperience(3)
                .workType(WorkType.REMOTE)
                .contactEmail("hr@acme.test")
                .categoryId(1L);

        assertThrows(ForbiddenException.class, () -> jobOfferService.updateJobOffer(request, jobOfferId, userId));
    }

    @Test
    void updateJobOffer_updatesSkillsAndDeletesMarked() {
        Long userId = 39L;
        Long jobOfferId = 12L;
        JobOfferEntity jobOffer = buildJobOffer(jobOfferId, userId);
        CategoryEntity category = buildCategory(90L, "Engineering");

        SkillEntity skillA = buildSkill(201L, "Java", new Float[]{1.0F, 0.0F});
        SkillEntity skillB = buildSkill(202L, "SQL", new Float[]{0.5F, 0.5F});
        SkillEntity skillC = buildSkill(203L, "Docker", new Float[]{0.2F, 0.8F});

        JobSkillEntity existingJobSkill = buildJobSkill(301L, jobOffer, skillA, SkillProficiency.BEGINNER);
        List<JobSkillEntity> existingJobSkills = new ArrayList<>(List.of(existingJobSkill));

        UpdateJobOfferRequest request = new UpdateJobOfferRequest()
                .title("Backend")
                .description("Desc")
                .companyName("Acme")
                .salary(1600.0F)
                .yearsOfExperience(4)
                .workType(WorkType.REMOTE)
                .contactEmail("hr@acme.test")
                .categoryId(category.getId())
                .skills(List.of(
                        new UpdateJobOfferSkill().skillId(skillA.getId()).proficiency(SkillProficiency.ADVANCED),
                        new UpdateJobOfferSkill().skillId(skillB.getId()).delete(true),
                        new UpdateJobOfferSkill().skillId(skillC.getId()).proficiency(SkillProficiency.INTERMEDIATE)
                ));

        when(jobOfferDao.findById(jobOfferId)).thenReturn(jobOffer);
        when(categoryService.findEntityById(category.getId())).thenReturn(category);
        when(jobOfferDao.save(jobOffer)).thenReturn(jobOffer);
        when(skillDao.findAllByIds(anyList())).thenReturn(Set.of(skillA, skillB, skillC));
        when(jobSkillDao.findAllByJobOfferId(jobOfferId)).thenReturn(existingJobSkills);
        when(jobSkillDao.saveAll(any())).thenReturn(existingJobSkills);
        when(jobSkillDao.findAllByJobOfferId(jobOfferId)).thenReturn(existingJobSkills);

        JobOffer response = jobOfferService.updateJobOffer(request, jobOfferId, userId);

        assertEquals(jobOfferId, response.getId());
        verify(jobSkillDao).deleteAllByIds(eq(List.of(skillB.getId())), eq(jobOfferId));
        assertEquals(SkillProficiency.ADVANCED, existingJobSkill.getExpectedProficiency());
    }

    @Test
    void deleteJobOffer_ownerDeletes() {
        Long userId = 40L;
        Long jobOfferId = 13L;
        JobOfferEntity jobOffer = buildJobOffer(jobOfferId, userId);

        when(jobOfferDao.findById(jobOfferId)).thenReturn(jobOffer);

        jobOfferService.deleteJobOffer(jobOfferId, userId);

        verify(jobOfferDao).delete(jobOffer);
    }

    @Test
    void deleteJobOffer_nonOwnerThrows() {
        Long userId = 41L;
        Long jobOfferId = 14L;
        JobOfferEntity jobOffer = buildJobOffer(jobOfferId, 999L);

        when(jobOfferDao.findById(jobOfferId)).thenReturn(jobOffer);

        assertThrows(ForbiddenException.class, () -> jobOfferService.deleteJobOffer(jobOfferId, userId));
    }

    @Test
    void applyToJobOffer_successReturnsApplication() {
        Long userId = 42L;
        Long jobOfferId = 15L;
        UserEntity applicant = buildUser(userId, "Nina", "Miles", "nina@jobly.test", "nina");
        JobOfferEntity jobOffer = buildJobOffer(jobOfferId, 500L);
        UserCvEntity cv = buildUserCv(66L, applicant, "cv.pdf", new byte[]{1});

        ApplicationCreateRequest request = new ApplicationCreateRequest().comment("Hello");
        ApplicationEntity saved = ApplicationEntity.builder()
                .id(901L)
                .status(ApplicationStatus.PENDING)
                .applicant(applicant)
                .jobOffer(jobOffer)
                .comment("Hello")
                .build();

        when(userService.findById(userId)).thenReturn(applicant);
        when(jobOfferDao.findById(jobOfferId)).thenReturn(jobOffer);
        when(cvDao.findMostRecentCv(userId)).thenReturn(cv);
        when(applicationDao.isUserAlreadyAppliedToJobOffer(userId, jobOfferId)).thenReturn(false);
        when(userProfileService.isUserProfileReviewed(userId)).thenReturn(true);
        when(applicationDao.saveApplication(any(ApplicationEntity.class))).thenReturn(saved);

        Application response = jobOfferService.applyToJobOffer(jobOfferId, userId, request);

        assertEquals(saved.getId(), response.getId());
        assertEquals(cv.getId(), response.getCvId());
    }

    @Test
    void applyToJobOffer_alreadyAppliedThrows() {
        Long userId = 43L;
        Long jobOfferId = 16L;

        when(applicationDao.isUserAlreadyAppliedToJobOffer(userId, jobOfferId)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> jobOfferService.applyToJobOffer(jobOfferId, userId, new ApplicationCreateRequest()));
    }

    @Test
    void applyToJobOffer_missingCvAndProfileThrows() {
        Long userId = 44L;
        Long jobOfferId = 17L;

        when(applicationDao.isUserAlreadyAppliedToJobOffer(userId, jobOfferId)).thenReturn(false);
        when(cvDao.findMostRecentCv(userId)).thenReturn(null);
        when(userProfileService.isUserProfileSetUp(userId)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> jobOfferService.applyToJobOffer(jobOfferId, userId, new ApplicationCreateRequest()));
    }

    @Test
    void applyToJobOffer_profileNotReviewedThrows() {
        Long userId = 45L;
        Long jobOfferId = 18L;
        UserCvEntity cv = buildUserCv(70L, buildUser(userId, "Ava", "Nord", "ava@jobly.test", "ava"), "cv.pdf", new byte[]{1});

        when(applicationDao.isUserAlreadyAppliedToJobOffer(userId, jobOfferId)).thenReturn(false);
        when(cvDao.findMostRecentCv(userId)).thenReturn(cv);
        when(userProfileService.isUserProfileReviewed(userId)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> jobOfferService.applyToJobOffer(jobOfferId, userId, new ApplicationCreateRequest()));
    }

    @Test
    void canApplyToJobOffer_alreadyAppliedReturnsFalse() {
        Long userId = 46L;
        Long jobOfferId = 19L;

        when(applicationDao.isUserAlreadyAppliedToJobOffer(userId, jobOfferId)).thenReturn(true);

        CanApplyResponse response = jobOfferService.canApplyToJobOffer(jobOfferId, userId);

        assertFalse(response.getCanApply());
        assertEquals(ApplicationRestrictionReason.ALREADY_APPLIED, response.getReason());
    }

    @Test
    void canApplyToJobOffer_missingCvAndProfileReturnsFalse() {
        Long userId = 47L;
        Long jobOfferId = 20L;

        when(applicationDao.isUserAlreadyAppliedToJobOffer(userId, jobOfferId)).thenReturn(false);
        when(userProfileService.isUserProfileSetUp(userId)).thenReturn(false);
        when(cvDao.existsByUserId(userId)).thenReturn(false);

        CanApplyResponse response = jobOfferService.canApplyToJobOffer(jobOfferId, userId);

        assertFalse(response.getCanApply());
        assertEquals(ApplicationRestrictionReason.SKILL_OR_CV_MISSING, response.getReason());
    }

    @Test
    void canApplyToJobOffer_profileNotReviewedReturnsFalse() {
        Long userId = 48L;
        Long jobOfferId = 21L;

        when(applicationDao.isUserAlreadyAppliedToJobOffer(userId, jobOfferId)).thenReturn(false);
        when(userProfileService.isUserProfileSetUp(userId)).thenReturn(true);
        when(userProfileService.isUserProfileReviewed(userId)).thenReturn(false);

        CanApplyResponse response = jobOfferService.canApplyToJobOffer(jobOfferId, userId);

        assertFalse(response.getCanApply());
        assertEquals(ApplicationRestrictionReason.SKILLS_NOT_VERIFIED, response.getReason());
    }

    @Test
    void canApplyToJobOffer_successReturnsTrue() {
        Long userId = 49L;
        Long jobOfferId = 22L;

        when(applicationDao.isUserAlreadyAppliedToJobOffer(userId, jobOfferId)).thenReturn(false);
        when(userProfileService.isUserProfileSetUp(userId)).thenReturn(true);
        when(userProfileService.isUserProfileReviewed(userId)).thenReturn(true);

        CanApplyResponse response = jobOfferService.canApplyToJobOffer(jobOfferId, userId);

        assertTrue(response.getCanApply());
    }
}

