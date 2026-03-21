package com.jobly.service;

import com.jobly.dao.CvDao;
import com.jobly.enums.CvStatus;
import com.jobly.exception.general.ForbiddenException;
import com.jobly.gen.api.parser.data.CvParseResponse;
import com.jobly.gen.model.CvUploadResponse;
import com.jobly.gen.model.GetUserDetailsResponse;
import com.jobly.model.UserCvEntity;
import com.jobly.model.UserEntity;
import com.jobly.service.api.CvParserApiService;
import com.jobly.service.handler.CvParseHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.jobly.util.TestEntityFactory.buildUser;
import static com.jobly.util.TestEntityFactory.buildUserCv;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CvServiceTests {

    @Mock
    private UserService userService;

    @Mock
    private CvDao cvDao;

    @Mock
    private CvParserApiService cvParserApiService;

    @Mock
    private CvParseHandler cvParseHandler;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private CvService cvService;

    @Test
    void uploadUserCv_inactivatesExistingAndSavesParsedCv() {
        Long userId = 40L;
        UserEntity user = buildUser(userId, "John", "Doe", "john@jobly.test", "johnny");
        byte[] bytes = "cv".getBytes(StandardCharsets.UTF_8);
        Resource resource = new ByteArrayResource(bytes);
        UserCvEntity existing = buildUserCv(1L, user, "old.pdf", "old".getBytes(StandardCharsets.UTF_8));
        UserCvEntity existingSecond = buildUserCv(2L, user, "old2.pdf", "old2".getBytes(StandardCharsets.UTF_8));

        when(file.getResource()).thenReturn(resource);
        when(userService.findById(userId)).thenReturn(user);
        when(cvDao.findAllByUserId(userId)).thenReturn(List.of(existing, existingSecond));
        CvParseResponse parseResponse = new CvParseResponse();
        when(cvParserApiService.parseCv(any())).thenReturn(parseResponse);

        UserCvEntity saved = buildUserCv(99L, user, "cv_John_Doe.pdf", bytes);
        when(cvDao.save(any(UserCvEntity.class))).thenReturn(saved);

        CvUploadResponse response = cvService.uploadUserCv(userId, file);

        assertEquals(99L, response.getCvId());
        assertEquals(CvStatus.INACTIVE, existing.getStatus());
        assertEquals(CvStatus.INACTIVE, existingSecond.getStatus());

        ArgumentCaptor<UserCvEntity> savedCaptor = ArgumentCaptor.forClass(UserCvEntity.class);
        verify(cvDao).save(savedCaptor.capture());
        verify(cvParseHandler).saveParsedCvData(savedCaptor.getValue(), parseResponse);
    }

    @Test
    void uploadUserCv_whenParsePreparationFails_throwsRuntimeException() throws IOException {
        Long userId = 41L;
        UserEntity user = buildUser(userId, "Jane", "Roe", "jane@jobly.test", "jane");
        byte[] bytes = "cv".getBytes(StandardCharsets.UTF_8);
        Resource resource = org.mockito.Mockito.mock(Resource.class);

        when(resource.getContentAsByteArray()).thenReturn(bytes).thenThrow(new IOException("boom"));
        when(file.getResource()).thenReturn(resource);
        when(userService.findById(userId)).thenReturn(user);
        when(cvDao.findAllByUserId(userId)).thenReturn(List.of());

        assertThrows(RuntimeException.class, () -> cvService.uploadUserCv(userId, file));

        verify(cvParserApiService, never()).parseCv(any());
        verify(cvDao, never()).save(any(UserCvEntity.class));
    }

    @Test
    void downloadUserCv_allowsOwnerAccess() {
        Long userId = 50L;
        Long cvId = 70L;
        UserEntity user = buildUser(userId, "Chris", "Snow", "chris@jobly.test", "chris");
        byte[] fileData = "pdf".getBytes(StandardCharsets.UTF_8);
        UserCvEntity cv = buildUserCv(cvId, user, "cv_chris.pdf", fileData);

        when(cvDao.findById(cvId)).thenReturn(cv);

        ResponseEntity<Resource> response = cvService.downloadUserCv(cvId, userId);

        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertEquals(fileData.length, response.getHeaders().getContentLength());
        assertNotNull(response.getBody());
        String contentDisposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
        assertNotNull(contentDisposition);
        assertEquals(true, contentDisposition.contains("cv_chris.pdf"));
    }

    @Test
    void downloadUserCv_nonOwnerNonEmployerThrows() {
        Long userId = 51L;
        Long cvId = 71L;
        UserEntity owner = buildUser(999L, "Other", "User", "other@jobly.test", "other");
        UserCvEntity cv = buildUserCv(cvId, owner, "cv_other.pdf", "pdf".getBytes(StandardCharsets.UTF_8));

        when(cvDao.findById(cvId)).thenReturn(cv);
        when(userService.isCurrentUserEmployer(userId)).thenReturn(false);

        assertThrows(ForbiddenException.class, () -> cvService.downloadUserCv(cvId, userId));
    }

    @Test
    void findActiveCvByUserId_delegatesToDao() {
        Long userId = 60L;
        UserEntity user = buildUser(userId, "Sam", "Hill", "sam@jobly.test", "sam");
        UserCvEntity cv = buildUserCv(88L, user, "cv_sam.pdf", "data".getBytes(StandardCharsets.UTF_8));

        when(cvDao.findByUserIdAndStatus(userId, CvStatus.ACTIVE)).thenReturn(cv);

        UserCvEntity response = cvService.findActiveCvByUserId(userId);

        assertEquals(cv, response);
    }

    @Test
    void getUserDetailsWithCv_returnsCvIdWhenPresent() {
        Long userId = 61L;
        UserEntity user = buildUser(userId, "Mia", "Ray", "mia@jobly.test", "mia");
        UserCvEntity cv = buildUserCv(89L, user, "cv_mia.pdf", "data".getBytes(StandardCharsets.UTF_8));

        when(userService.findById(userId)).thenReturn(user);
        when(cvDao.findOptionalByUserIdAndStatus(userId, CvStatus.ACTIVE)).thenReturn(cv);

        GetUserDetailsResponse response = cvService.getUserDetailsWithCv(userId);

        assertEquals(userId, response.getId());
        assertEquals(user.getFirstName(), response.getFirstName());
        assertEquals(user.getLastName(), response.getLastName());
        assertEquals(user.getDisplayName(), response.getUsername());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(cv.getId(), response.getCvId());
    }

    @Test
    void getUserDetailsWithCv_returnsNullCvIdWhenMissing() {
        Long userId = 62L;
        UserEntity user = buildUser(userId, "Lee", "Poe", "lee@jobly.test", "lee");

        when(userService.findById(userId)).thenReturn(user);
        when(cvDao.findOptionalByUserIdAndStatus(userId, CvStatus.ACTIVE)).thenReturn(null);

        GetUserDetailsResponse response = cvService.getUserDetailsWithCv(userId);

        assertEquals(userId, response.getId());
        assertNull(response.getCvId());
    }
}
