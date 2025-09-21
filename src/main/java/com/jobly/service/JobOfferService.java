package com.jobly.service;

import com.jobly.dao.JobOfferDao;
import com.jobly.exception.general.ForbiddenException;
import com.jobly.gen.model.*;
import com.jobly.mapper.JobOfferMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobOfferService {

    private final JobOfferDao jobOfferDao;
    private final UserService userService;
    private final CategoryService categoryService;

    public GetAllJobOffersResponse findAll() {
        var jobOffers = jobOfferDao.findAll().stream()
                .map(JobOfferMapper::toJobOfferListObject)
                .toList();

        return new GetAllJobOffersResponse()
                .jobOffers(jobOffers)
                .total(jobOffers.size());
    }

    public JobOfferDetailsResponse findById(Long id) {
        var jobOffer = jobOfferDao.findById(id);
        return JobOfferMapper.toJobOfferDetailsResponse(jobOffer);
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    public GetMineJobOffersResponse findJobOffersByUserId(Long userId) {
        var jobOffers = jobOfferDao.findByUserId(userId).stream()
                .map(JobOfferMapper::toJobOfferListObject)
                .toList();

        return new GetMineJobOffersResponse()
                .jobOffers(jobOffers)
                .total(jobOffers.size());
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    public JobOfferWithApplicationsResponse findOwnedJobOfferDetails(Long id, Long userId) {
        var jobOffer = jobOfferDao.findById(id);
        if (!jobOffer.getCreator().getId().equals(userId)) {
            throw new ForbiddenException("User is not the owner of the job offer");
        }
        return JobOfferMapper.toJobOfferWithApplicationsResponse(jobOffer);
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    public JobOffer createJobOffer(CreateJobOfferRequest createJobOfferRequest, Long userId) {
        var user = userService.findById(userId);
        var category = categoryService.findEntityById(createJobOfferRequest.getCategoryId());
        var jobOffer = JobOfferMapper.toJobOfferEntity(createJobOfferRequest, user, category);
        return JobOfferMapper.toJobOffer(jobOfferDao.save(jobOffer));
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    public JobOffer updateJobOffer(UpdateJobOfferRequest updateJobOfferRequest, Long jobOfferId, Long userId) {
        var jobOffer = jobOfferDao.findById(jobOfferId);
        if (!jobOffer.getCreator().getId().equals(userId)) {
            throw new ForbiddenException("User is not the owner of the job offer");
        }
        var category = categoryService.findEntityById(updateJobOfferRequest.getCategoryId());
        JobOfferMapper.updateJobOfferEntity(jobOffer, updateJobOfferRequest, category);
        return JobOfferMapper.toJobOffer(jobOfferDao.save(jobOffer));
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    public void deleteJobOffer(Long id, Long userId) {
        var jobOffer = jobOfferDao.findById(id);
        if (!jobOffer.getCreator().getId().equals(userId)) {
            throw new ForbiddenException("User is not the owner of the job offer");
        }
        jobOfferDao.delete(jobOffer);
    }
}
