package com.jobly.mapper;

import com.jobly.enums.CvStatus;
import com.jobly.enums.FileType;
import com.jobly.exception.general.SystemException;
import com.jobly.gen.model.CvUploadResponse;
import com.jobly.model.UserCvEntity;
import com.jobly.model.UserEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CvMapper {

    public static UserCvEntity toUserCvEntity(Resource file, UserEntity user){
        var cvEntity = new UserCvEntity();
        cvEntity.setTitle("cv_" + user.getFirstName() + "_" + user.getLastName() + ".pdf");
        cvEntity.setFileType(FileType.PDF);
        cvEntity.setStatus(CvStatus.ACTIVE);
        cvEntity.setUser(user);
        try {
            cvEntity.setFileData(file.getContentAsByteArray());
        } catch (IOException e) {
            throw new SystemException("Failed to read CV file data");
        }
        return cvEntity;
    }

    public static CvUploadResponse toCvUploadResponse(UserCvEntity save) {
        return new CvUploadResponse()
                .cvId(save.getId());
    }
}
