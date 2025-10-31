package com.jobly.dao;

import com.jobly.exception.general.NotFoundException;
import com.jobly.gen.model.ApplicationStatus;
import com.jobly.model.ApplicationEntity;
import com.jobly.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationDao {

    private final ApplicationRepository applicationRepository;

    public ApplicationEntity saveApplication(ApplicationEntity application) {
        return applicationRepository.save(application);
    }

    public Collection<ApplicationEntity> findAllByUserId(Long userId) {
        return applicationRepository.findAllByApplicantId(userId);
    }

    public ApplicationEntity findApplicationOfUser(Long userId, Long applicationId) {
        return applicationRepository.findByApplicantIdAndId(userId, applicationId)
                .orElseThrow(() -> new NotFoundException("Application not found for user"));
    }

    public ApplicationEntity save(ApplicationEntity application) {
        return applicationRepository.save(application);
    }

    public ApplicationEntity findById(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application not found"));
    }

    public List<ApplicationEntity> findAllPendingByJobOfferId(Long id) {
        return applicationRepository.findAllByJobOfferIdAndStatus(id, ApplicationStatus.PENDING);
    }

    public boolean isUserAlreadyAppliedToJobOffer(Long userId, Long jobOfferId) {
        return applicationRepository.existsByApplicantIdAndJobOfferIdAndStatusIn(userId, jobOfferId,
                List.of(ApplicationStatus.ACCEPTED, ApplicationStatus.PENDING, ApplicationStatus.REJECTED));
    }
}
