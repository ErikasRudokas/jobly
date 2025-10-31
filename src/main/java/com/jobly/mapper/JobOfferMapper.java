package com.jobly.mapper;

import com.jobly.gen.model.*;
import com.jobly.model.ApplicationEntity;
import com.jobly.model.CategoryEntity;
import com.jobly.model.JobOfferEntity;
import com.jobly.model.UserEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JobOfferMapper {

    public static JobOffer toJobOffer(JobOfferEntity jobOfferEntity) {
        var jobOffer = new JobOffer();
        jobOffer.setId(jobOfferEntity.getId());
        jobOffer.setTitle(jobOfferEntity.getTitle());
        jobOffer.setDescription(jobOfferEntity.getDescription());
        jobOffer.setSalary(jobOfferEntity.getSalary().floatValue());
        jobOffer.setYearsOfExperience(jobOfferEntity.getYearsOfExperience());
        jobOffer.setCompanyName(jobOfferEntity.getCompany());
        jobOffer.setWorkType(jobOfferEntity.getWorkType());
        jobOffer.setLocation(jobOfferEntity.getLocation());
        jobOffer.setContactEmail(jobOfferEntity.getContactEmail());
        jobOffer.setContactPhone(jobOfferEntity.getContactPhone());
        jobOffer.setOfferStatus(jobOfferEntity.getStatus());
        jobOffer.setCreatedAt(jobOfferEntity.getCreatedAt());
        jobOffer.setUpdatedAt(jobOfferEntity.getUpdatedAt());
        jobOffer.setCategory(CategoryMapper.toJobOfferCategory(jobOfferEntity.getCategory()));
        jobOffer.setCreator(UserMapper.toCreator(jobOfferEntity.getCreator()));
        return jobOffer;
    }

    public static JobOfferListObject toJobOfferListObject(JobOfferEntity jobOfferEntity) {
        var jobOfferListObject = new JobOfferListObject();
        jobOfferListObject.setId(jobOfferEntity.getId());
        jobOfferListObject.setTitle(jobOfferEntity.getTitle());
        jobOfferListObject.setSalary(jobOfferEntity.getSalary().floatValue());
        jobOfferListObject.setWorkType(jobOfferEntity.getWorkType());
        jobOfferListObject.setLocation(jobOfferEntity.getLocation());
        jobOfferListObject.setCategory(CategoryMapper.toJobOfferCategory(jobOfferEntity.getCategory()));
        jobOfferListObject.setCompanyName(jobOfferEntity.getCompany());
        return jobOfferListObject;
    }

    public static JobOfferDetailsResponse toJobOfferDetailsResponse(JobOfferEntity jobOffer) {
        var jobOfferDetailsResponse = new JobOfferDetailsResponse();
        jobOfferDetailsResponse.setJobOffer(toJobOffer(jobOffer));
        return jobOfferDetailsResponse;
    }

    public static JobOfferWithApplicationsResponse toJobOfferWithApplicationsResponse(JobOfferEntity jobOffer, List<ApplicationEntity> applications) {
        var response = new JobOfferWithApplicationsResponse();
        response.setJobOffer(toJobOffer(jobOffer));
        response.setApplications(applications.stream().map(ApplicationMapper::toApplication).toList());
        return response;
    }

    public static JobOfferEntity toJobOfferEntity(CreateJobOfferRequest createJobOfferRequest, UserEntity user, CategoryEntity category) {
        var jobOfferEntity = new JobOfferEntity();
        jobOfferEntity.setTitle(createJobOfferRequest.getTitle());
        jobOfferEntity.setDescription(createJobOfferRequest.getDescription());
        jobOfferEntity.setSalary(BigDecimal.valueOf(createJobOfferRequest.getSalary()));
        jobOfferEntity.setYearsOfExperience(createJobOfferRequest.getYearsOfExperience());
        jobOfferEntity.setCompany(createJobOfferRequest.getCompanyName());
        jobOfferEntity.setWorkType(createJobOfferRequest.getWorkType());
        jobOfferEntity.setLocation(createJobOfferRequest.getLocation());
        jobOfferEntity.setContactEmail(createJobOfferRequest.getContactEmail());
        jobOfferEntity.setContactPhone(createJobOfferRequest.getContactPhone());
        jobOfferEntity.setCategory(category);
        jobOfferEntity.setCreator(user);
        jobOfferEntity.setStatus(JobOfferStatus.OPEN);
        return jobOfferEntity;
    }

    public static void updateJobOfferEntity(JobOfferEntity jobOffer, UpdateJobOfferRequest updateJobOfferRequest, CategoryEntity category) {
        jobOffer.setTitle(updateJobOfferRequest.getTitle());
        jobOffer.setDescription(updateJobOfferRequest.getDescription());
        jobOffer.setSalary(BigDecimal.valueOf(updateJobOfferRequest.getSalary()));
        jobOffer.setYearsOfExperience(updateJobOfferRequest.getYearsOfExperience());
        jobOffer.setCompany(updateJobOfferRequest.getCompanyName());
        jobOffer.setWorkType(updateJobOfferRequest.getWorkType());
        jobOffer.setLocation(updateJobOfferRequest.getLocation());
        jobOffer.setContactEmail(updateJobOfferRequest.getContactEmail());
        jobOffer.setContactPhone(updateJobOfferRequest.getContactPhone());
        jobOffer.setCategory(category);
    }
}
