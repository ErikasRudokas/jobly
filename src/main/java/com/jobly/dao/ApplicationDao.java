package com.jobly.dao;

import com.jobly.dto.MyApplicationFilterWrapper;
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

    public Collection<ApplicationEntity> findAllByUserId(Long userId, MyApplicationFilterWrapper filterWrapper) {
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

    public Integer getCountOfAllApplicationsByJobOfferId(Long jobOfferId) {
        return applicationRepository.countAllByJobOfferId(jobOfferId);
    }

    public boolean isUserAlreadyAppliedToJobOffer(Long userId, Long jobOfferId) {
        return applicationRepository.existsByApplicantIdAndJobOfferIdAndStatusIn(userId, jobOfferId,
                List.of(ApplicationStatus.ACCEPTED, ApplicationStatus.PENDING, ApplicationStatus.REJECTED));
    }

    public List<ApplicationEntity> findAllApplicationsByJobOffer(Long jobOfferId) {
        return applicationRepository.findAllByJobOfferId(jobOfferId);
    }

    public Integer countAllByUserId(Long userId, MyApplicationFilterWrapper filterWrapper) {
        ApplicationStatus status = filterWrapper.getStatus();
        List<ApplicationStatus> statuses = status != null ? List.of(status) : List.of(ApplicationStatus.values());
        List<String> formattedStatuses = formatApplicationStatuses(statuses);
        return applicationRepository.countAllByUserIdAndFilter(userId, formattedStatuses);
    }

    private static List<String> formatApplicationStatuses(List<ApplicationStatus> statuses) {
        return statuses.stream()
                .map(ApplicationStatus::getValue)
                .toList();
    }
}
