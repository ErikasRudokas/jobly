package com.jobly.mapper;

import com.jobly.gen.model.*;
import com.jobly.model.ApplicationEntity;
import com.jobly.model.JobOfferEntity;
import com.jobly.model.UserCvEntity;
import com.jobly.model.UserEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationMapper {
    public static ApplicationEntity toApplicationEntity(ApplicationCreateRequest request, UserEntity applicant, JobOfferEntity jobOffer, UserCvEntity cv) {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setStatus(ApplicationStatus.PENDING);
        applicationEntity.setComment(request.getComment());
        applicationEntity.setApplicant(applicant);
        applicationEntity.setJobOffer(jobOffer);
        applicationEntity.setUserCv(cv);
        return applicationEntity;
    }

    public static Application toApplication(ApplicationEntity applicationEntity) {
        var application = new Application();
        application.setId(applicationEntity.getId());
        application.setComment(applicationEntity.getComment());
        application.setCreatedAt(applicationEntity.getCreatedAt());
        application.setUpdatedAt(applicationEntity.getUpdatedAt());
        application.setCvId(applicationEntity.getUserCv().getId());
        application.setApplicationStatus(applicationEntity.getStatus());
        application.setApplicant(UserMapper.toApplicant(applicationEntity.getApplicant()));
        return application;
    }

    public static MyApplicationListObject toApplicationListObject(ApplicationEntity application) {
        var applicationListObject = new MyApplicationListObject();
        applicationListObject.setId(application.getId());
        applicationListObject.updatedAt(application.getUpdatedAt());
        applicationListObject.setApplicationStatus(application.getStatus());
        applicationListObject.setJobOffer(JobOfferMapper.toJobOfferListObject(application.getJobOffer()));
        return applicationListObject;
    }

    public static MyApplication toMyApplication(ApplicationEntity application) {
        var myApplication = new MyApplication();
        myApplication.setId(application.getId());
        myApplication.setComment(application.getComment());
        myApplication.setCreatedAt(application.getCreatedAt());
        myApplication.setUpdatedAt(application.getUpdatedAt());
        myApplication.setJobOffer(JobOfferMapper.toJobOfferListObject(application.getJobOffer()));
        myApplication.setCvId(application.getUserCv().getId());
        return myApplication;
    }
}
