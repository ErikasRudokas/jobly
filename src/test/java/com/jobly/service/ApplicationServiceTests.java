package com.jobly.service;

import com.jobly.dao.ApplicationDao;
import com.jobly.dao.CvDao;
import com.jobly.dto.ApplicationFilterWrapper;
import com.jobly.exception.general.BadRequestException;
import com.jobly.exception.general.ForbiddenException;
import com.jobly.gen.model.*;
import com.jobly.model.ApplicationEntity;
import com.jobly.model.JobOfferEntity;
import com.jobly.model.UserCvEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.jobly.util.TestEntityFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTests {

    @Mock
    private ApplicationDao applicationDao;

    @Mock
    private CvDao cvDao;

    @InjectMocks
    private ApplicationService applicationService;

    @Test
    void getMyApplications_mapsApplicationsAndTotal() {
        Long userId = 11L;
        ApplicationFilterWrapper filter = ApplicationFilterWrapper.builder()
                .status(ApplicationStatus.PENDING)
                .offset(0)
                .limit(10)
                .build();

        JobOfferEntity jobOffer = buildJobOffer(200L, 300L);
        ApplicationEntity first = buildApplication(1L, ApplicationStatus.PENDING, jobOffer);
        ApplicationEntity second = buildApplication(2L, ApplicationStatus.REJECTED, jobOffer);

        when(applicationDao.findAllByUserId(userId, filter)).thenReturn(List.of(first, second));
        when(applicationDao.countAllByUserId(userId, filter)).thenReturn(2);

        GetMyApplicationsResponse response = applicationService.getMyApplications(userId, filter);

        assertEquals(2, response.getTotal());
        assertNotNull(response.getApplications());
        assertEquals(2, response.getApplications().size());

        MyApplicationListObject mapped = response.getApplications().get(0);
        assertEquals(first.getId(), mapped.getId());
        assertEquals(first.getStatus(), mapped.getApplicationStatus());
        assertEquals(jobOffer.getId(), mapped.getJobOffer().getId());
        assertEquals(jobOffer.getCompany(), mapped.getJobOffer().getCompanyName());
    }

    @Test
    void getMyApplication_returnsCvIdWhenAvailable() {
        Long userId = 12L;
        Long applicationId = 55L;
        JobOfferEntity jobOffer = buildJobOffer(201L, 301L);
        ApplicationEntity application = buildApplication(applicationId, ApplicationStatus.PENDING, jobOffer);
        application.setComment("Updated details");

        UserCvEntity cv = UserCvEntity.builder().id(44L).build();

        when(applicationDao.findApplicationOfUser(userId, applicationId)).thenReturn(application);
        when(cvDao.findMostRecentCv(userId)).thenReturn(cv);

        MyApplication response = applicationService.getMyApplication(applicationId, userId);

        assertEquals(applicationId, response.getId());
        assertEquals(application.getStatus(), response.getApplicationStatus());
        assertEquals(cv.getId(), response.getCvId());
    }

    @Test
    void getMyApplication_returnsNullCvIdWhenMissing() {
        Long userId = 13L;
        Long applicationId = 56L;
        JobOfferEntity jobOffer = buildJobOffer(202L, 302L);
        ApplicationEntity application = buildApplication(applicationId, ApplicationStatus.PENDING, jobOffer);

        when(applicationDao.findApplicationOfUser(userId, applicationId)).thenReturn(application);
        when(cvDao.findMostRecentCv(userId)).thenReturn(null);

        MyApplication response = applicationService.getMyApplication(applicationId, userId);

        assertEquals(applicationId, response.getId());
        assertNull(response.getCvId());
    }

    @Test
    void cancelApplication_pendingUpdatesStatusAndSaves() {
        Long userId = 14L;
        Long applicationId = 57L;
        ApplicationEntity application = ApplicationEntity.builder()
                .id(applicationId)
                .status(ApplicationStatus.PENDING)
                .build();

        when(applicationDao.findApplicationOfUser(userId, applicationId)).thenReturn(application);

        applicationService.cancelApplication(applicationId, userId);

        assertEquals(ApplicationStatus.WITHDRAWN, application.getStatus());
        verify(applicationDao).save(application);
    }

    @Test
    void cancelApplication_nonPendingThrows() {
        Long userId = 15L;
        Long applicationId = 58L;
        ApplicationEntity application = ApplicationEntity.builder()
                .id(applicationId)
                .status(ApplicationStatus.ACCEPTED)
                .build();

        when(applicationDao.findApplicationOfUser(userId, applicationId)).thenReturn(application);

        assertThrows(BadRequestException.class, () -> applicationService.cancelApplication(applicationId, userId));
        verify(applicationDao, never()).save(application);
    }

    @Test
    void updateApplication_pendingUpdatesCommentAndReturnsMapped() {
        Long userId = 16L;
        Long applicationId = 59L;
        ApplicationEntity application = ApplicationEntity.builder()
                .id(applicationId)
                .status(ApplicationStatus.PENDING)
                .comment("Old")
                .applicant(buildApplicant(700L))
                .build();

        ApplicationUpdateRequest request = new ApplicationUpdateRequest().comment("New comment");
        UserCvEntity cv = UserCvEntity.builder().id(99L).build();

        when(applicationDao.findApplicationOfUser(userId, applicationId)).thenReturn(application);
        when(applicationDao.save(application)).thenReturn(application);
        when(cvDao.findMostRecentCv(userId)).thenReturn(cv);

        Application response = applicationService.updateApplication(applicationId, userId, request);

        assertEquals("New comment", application.getComment());
        assertEquals(applicationId, response.getId());
        assertEquals(ApplicationStatus.PENDING, response.getApplicationStatus());
        assertEquals(cv.getId(), response.getCvId());
    }

    @Test
    void updateApplication_nonPendingThrows() {
        Long userId = 17L;
        Long applicationId = 60L;
        ApplicationEntity application = ApplicationEntity.builder()
                .id(applicationId)
                .status(ApplicationStatus.REJECTED)
                .build();

        when(applicationDao.findApplicationOfUser(userId, applicationId)).thenReturn(application);

        ApplicationUpdateRequest request = new ApplicationUpdateRequest().comment("New comment");

        assertThrows(BadRequestException.class, () -> applicationService.updateApplication(applicationId, userId, request));
        verify(applicationDao, never()).save(application);
    }

    @Test
    void manageApplication_approveUpdatesStatusAndSaves() {
        Long userId = 18L;
        Long applicationId = 61L;
        ApplicationEntity application = buildManageableApplication(applicationId, userId, ApplicationStatus.PENDING);
        ApplicationManageRequest request = new ApplicationManageRequest().action(ApplicationManageAction.APPROVE);

        when(applicationDao.findById(applicationId)).thenReturn(application);

        applicationService.manageApplication(applicationId, userId, request);

        assertEquals(ApplicationStatus.ACCEPTED, application.getStatus());
        verify(applicationDao).save(application);
    }

    @Test
    void manageApplication_rejectUpdatesStatusAndSaves() {
        Long userId = 19L;
        Long applicationId = 62L;
        ApplicationEntity application = buildManageableApplication(applicationId, userId, ApplicationStatus.PENDING);
        ApplicationManageRequest request = new ApplicationManageRequest().action(ApplicationManageAction.REJECT);

        when(applicationDao.findById(applicationId)).thenReturn(application);

        applicationService.manageApplication(applicationId, userId, request);

        assertEquals(ApplicationStatus.REJECTED, application.getStatus());
        verify(applicationDao).save(application);
    }

    @Test
    void manageApplication_wrongCreatorThrowsForbidden() {
        Long userId = 20L;
        Long applicationId = 63L;
        ApplicationEntity application = buildManageableApplication(applicationId, 999L, ApplicationStatus.PENDING);
        ApplicationManageRequest request = new ApplicationManageRequest().action(ApplicationManageAction.REJECT);

        when(applicationDao.findById(applicationId)).thenReturn(application);

        assertThrows(ForbiddenException.class, () -> applicationService.manageApplication(applicationId, userId, request));
        verify(applicationDao, never()).save(application);
    }

    @Test
    void manageApplication_nonPendingThrowsBadRequest() {
        Long userId = 21L;
        Long applicationId = 64L;
        ApplicationEntity application = buildManageableApplication(applicationId, userId, ApplicationStatus.ACCEPTED);
        ApplicationManageRequest request = new ApplicationManageRequest().action(ApplicationManageAction.REJECT);

        when(applicationDao.findById(applicationId)).thenReturn(application);

        assertThrows(BadRequestException.class, () -> applicationService.manageApplication(applicationId, userId, request));
        verify(applicationDao, never()).save(application);
    }
}
