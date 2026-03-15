package com.jobly.dao;

import com.jobly.dto.ApplicationFilterWrapper;
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

    private static final List<ApplicationStatus> EMPLOYER_RELEVANT_STATUSES = List.of(
            ApplicationStatus.ACCEPTED,
            ApplicationStatus.PENDING,
            ApplicationStatus.REJECTED);

    private final ApplicationRepository applicationRepository;

    public ApplicationEntity saveApplication(ApplicationEntity application) {
        return applicationRepository.save(application);
    }

    public Collection<ApplicationEntity> findAllByUserId(Long userId, ApplicationFilterWrapper filterWrapper) {
        ApplicationStatus status = filterWrapper.getStatus();
        Integer offset = filterWrapper.getOffset();
        Integer limit = filterWrapper.getLimit();

        int defaultOffset = offset != null ? offset : 0;
        int defaultLimit = limit != null ? limit : 10;
        List<ApplicationStatus> statuses = status != null ? List.of(status) : List.of(ApplicationStatus.values());
        List<String> statusStrings = formatApplicationStatuses(statuses);
        return applicationRepository.findAllByUserIdAndFilter(userId, statusStrings, defaultLimit, defaultOffset);
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

    public Integer getCountOfAllPendingByJobOfferId(Long jobOfferId) {
        return applicationRepository.countAllByJobOfferIdAndStatus(jobOfferId, ApplicationStatus.PENDING);
    }

    public Integer getCountOfAllRelevantApplicationsByJobOfferId(Long jobOfferId) {
        return applicationRepository.countAllByJobOfferIdAndStatusIn(jobOfferId, EMPLOYER_RELEVANT_STATUSES);
    }

    public boolean isUserAlreadyAppliedToJobOffer(Long userId, Long jobOfferId) {
        return applicationRepository.existsByApplicantIdAndJobOfferIdAndStatusIn(userId, jobOfferId,
                List.of(ApplicationStatus.ACCEPTED, ApplicationStatus.PENDING, ApplicationStatus.REJECTED));
    }

    public List<ApplicationEntity> findAllApplicationsByJobOffer(Long jobOfferId) {
        return applicationRepository.findAllByJobOfferIdAndStatusIn(jobOfferId, EMPLOYER_RELEVANT_STATUSES);
    }

    public Integer countAllByUserId(Long userId, ApplicationFilterWrapper filterWrapper) {
        ApplicationStatus status = filterWrapper.getStatus();
        List<ApplicationStatus> statuses = status != null ? List.of(status) : List.of(ApplicationStatus.values());
        List<String> formattedStatuses = formatApplicationStatuses(statuses);
        return applicationRepository.countAllByUserIdAndFilter(userId, formattedStatuses);
    }

    public List<ApplicationEntity> findAllByJobOfferId(Long jobOfferId, ApplicationFilterWrapper filterWrapper) {
        ApplicationStatus status = filterWrapper.getStatus();
        Integer offset = filterWrapper.getOffset();
        Integer limit = filterWrapper.getLimit();

        int defaultOffset = offset != null ? offset : 0;
        int defaultLimit = limit != null ? limit : 10;
        List<ApplicationStatus> statuses = status != null ? List.of(status) : EMPLOYER_RELEVANT_STATUSES;
        List<String> statusStrings = formatApplicationStatuses(statuses);
        return applicationRepository.findAllByJobOfferIdAndFilter(jobOfferId, statusStrings, defaultLimit, defaultOffset);
    }

    public Integer countAllByJobOfferId(Long jobOfferId, ApplicationFilterWrapper filterWrapper) {
        ApplicationStatus status = filterWrapper.getStatus();
        List<ApplicationStatus> statuses = status != null ? List.of(status) : EMPLOYER_RELEVANT_STATUSES;
        List<String> formattedStatuses = formatApplicationStatuses(statuses);
        return applicationRepository.countAllByJobOfferIdAndFilter(jobOfferId, formattedStatuses);
    }

    private static List<String> formatApplicationStatuses(List<ApplicationStatus> statuses) {
        return statuses.stream()
                .map(ApplicationStatus::getValue)
                .toList();
    }
}
