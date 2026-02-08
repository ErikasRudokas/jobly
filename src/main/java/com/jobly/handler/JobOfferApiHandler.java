package com.jobly.handler;

import com.jobly.gen.api.JobOffersApiDelegate;
import com.jobly.gen.model.*;
import com.jobly.security.service.JwtService;
import com.jobly.service.JobOfferService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobOfferApiHandler implements JobOffersApiDelegate {

    private final JobOfferService jobOfferService;
    private final HttpServletRequest httpServletRequest;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<GetAllJobOffersResponse> getAllJobOffers() {
        log.info("Getting all the job offers");
        return ResponseEntity.ok(jobOfferService.findAll());
    }

    @Override
    public ResponseEntity<JobOfferDetailsResponse> getJobOfferById(Long id) {
        log.info("Getting the job offer with id {}", id);
        return ResponseEntity.ok(jobOfferService.findById(id));
    }

    @Override
    public ResponseEntity<GetMineJobOffersResponse> getOwnedJobOffers() {
        var userId = jwtService.extractUserId(httpServletRequest);
        log.info("Getting owned job offers for user with id {}", userId);
        return ResponseEntity.ok(jobOfferService.findJobOffersByUserId(userId));
    }

    @Override
    public ResponseEntity<JobOfferWithApplicationsResponse> getOwnedJobOfferDetails(Long id) {
        var userId = jwtService.extractUserId(httpServletRequest);
        log.info("Getting owned job offer details with id {}", id);
        return ResponseEntity.ok(jobOfferService.findOwnedJobOfferDetails(id, userId));
    }

    @Override
    public ResponseEntity<JobOffer> createJobOffer(CreateJobOfferRequest createJobOfferRequest) {
        log.info("Creating a new job offer");
        var userId = jwtService.extractUserId(httpServletRequest);
        var jobOffer = jobOfferService.createJobOffer(createJobOfferRequest, userId);
        return ResponseEntity.created(URI.create("/api/v1/job-offers/" + jobOffer.getId()))
                .body(jobOffer);
    }

    @Override
    public ResponseEntity<JobOffer> updateJobOffer(Long id, UpdateJobOfferRequest updateJobOfferRequest) {
        log.info("Updating a new job offer with id {}", id);
        var userId = jwtService.extractUserId(httpServletRequest);
        return ResponseEntity.ok(jobOfferService.updateJobOffer(updateJobOfferRequest, id, userId));
    }

    @Override
    public ResponseEntity<Void> deleteJobOffer(Long id) {
        log.info("Deleting the job offer with id {}", id);
        var userId = jwtService.extractUserId(httpServletRequest);
        jobOfferService.deleteJobOffer(id, userId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Application> applyToJobOffer(Long jobOfferId, ApplicationCreateRequest applicationCreateRequest) {
        var userId = jwtService.extractUserId(httpServletRequest);
        log.info("User with id {} is applying to job offer with id {}", userId, jobOfferId);
        var application = jobOfferService.applyToJobOffer(jobOfferId, userId, applicationCreateRequest);
        return ResponseEntity.created(URI.create("/applications/mine/" + application.getId()))
                .body(application);
    }

    @Override
    public ResponseEntity<CanApplyResponse> canApplyToJobOffer(Long jobOfferId) {
        var userId = jwtService.extractUserId(httpServletRequest);
        log.info("Checking if user with id {} can apply to job offer with id {}", userId, jobOfferId);
        var canApplyResponse = jobOfferService.canApplyToJobOffer(jobOfferId, userId);
        return ResponseEntity.ok(canApplyResponse);
    }
}
