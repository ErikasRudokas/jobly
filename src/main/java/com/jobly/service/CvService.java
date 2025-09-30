package com.jobly.service;

import com.jobly.dao.CvDao;
import com.jobly.enums.CvStatus;
import com.jobly.gen.model.CvUploadResponse;
import com.jobly.mapper.CvMapper;
import com.jobly.model.UserCvEntity;
import com.jobly.util.HttpHeaderConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class CvService {
    
    private final UserService userService;
    private final CvDao cvDao;

    public CvUploadResponse uploadUserCv(Long userId, MultipartFile file){
        Resource cvFile = file.getResource();
        var user = userService.findById(userId);

        var userCvEntity = CvMapper.toUserCvEntity(cvFile, user);

        var existingCvs = cvDao.findAllByUserId(userId);
        existingCvs.forEach(cv -> cv.setStatus(CvStatus.INACTIVE));
        cvDao.saveAll(existingCvs);

        return CvMapper.toCvUploadResponse(cvDao.save(userCvEntity));
    }

    @PreAuthorize("hasRole('EMPLOYER') or #requestedOnUserId == #userId")
    public ResponseEntity<Resource> downloadUserCv(Long requestedOnUserId, Long userId) {
        UserCvEntity userCvEntity = cvDao.findByUserIdAndStatus(requestedOnUserId, CvStatus.ACTIVE);

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
}
