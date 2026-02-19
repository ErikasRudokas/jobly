package com.jobly.service;

import com.jobly.dao.CvDao;
import com.jobly.enums.CvStatus;
import com.jobly.exception.general.ForbiddenException;
import com.jobly.gen.model.CvUploadResponse;
import com.jobly.gen.model.GetUserDetailsResponse;
import com.jobly.mapper.CvMapper;
import com.jobly.mapper.UserMapper;
import com.jobly.model.UserCvEntity;
import com.jobly.service.api.CvParserApiService;
import com.jobly.service.handler.CvParseHandler;
import com.jobly.util.HttpHeaderConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CvService {
    
    private final UserService userService;
    private final CvDao cvDao;
    private final CvParserApiService cvParserApiService;
    private final CvParseHandler cvParseHandler;

    public CvUploadResponse uploadUserCv(Long userId, MultipartFile file) {
        Resource cvFile = file.getResource();
        var user = userService.findById(userId);

        var userCvEntity = CvMapper.toUserCvEntity(cvFile, user);

        var existingCvs = cvDao.findAllByUserId(userId);
        existingCvs.forEach(cv -> cv.setStatus(CvStatus.INACTIVE));
        cvDao.saveAll(existingCvs);

        try {
            AbstractResource cvFileToParse = getCvFileToParse(cvFile, userCvEntity);
            var response = cvParserApiService.parseCv(cvFileToParse);
            cvParseHandler.saveParsedCvData(userCvEntity, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return CvMapper.toCvUploadResponse(cvDao.save(userCvEntity));
    }

    private static AbstractResource getCvFileToParse(Resource cvFile, UserCvEntity userCvEntity) throws IOException {
        return new ByteArrayResource(cvFile.getContentAsByteArray()) {
            @Override
            public String getFilename() {
                return userCvEntity.getTitle();
            }
        };
    }

    public ResponseEntity<Resource> downloadUserCv(Long cvId, Long userId) {
        UserCvEntity userCvEntity = cvDao.findById(cvId);
        if (!userCvEntity.getUser().getId().equals(userId) && !userService.isCurrentUserEmployer(userId)) {
            throw new ForbiddenException("Access denied: You do not have permission to access this CV.");
        }

        ContentDisposition contentDisposition = ContentDisposition.builder(HttpHeaderConstants.CONTENT_DISPOSITION_ATTACHMENT)
                .filename(userCvEntity.getTitle())
                .build();

        byte[] fileData = userCvEntity.getFileData();
        Resource resource = new ByteArrayResource(fileData);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentLength(fileData.length)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    public UserCvEntity findActiveCvByUserId(Long userId) {
        return cvDao.findByUserIdAndStatus(userId, CvStatus.ACTIVE);
    }

    public GetUserDetailsResponse getUserDetailsWithCv(Long userId) {
        var userCv = findOptionalActiveCvByUserId(userId);
        return UserMapper.toGetUserDetailsResponse(userService.findById(userId), userCv);
    }

    private UserCvEntity findOptionalActiveCvByUserId(Long userId) {
        return cvDao.findOptionalByUserIdAndStatus(userId, CvStatus.ACTIVE);
    }
}
