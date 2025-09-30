package com.jobly.repository;

import com.jobly.gen.model.ApplicationStatus;
import com.jobly.model.ApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {
    List<ApplicationEntity> findAllByApplicantId(Long userId);

    Optional<ApplicationEntity> findByApplicantIdAndId(Long applicantId, Long id);

    List<ApplicationEntity> findAllByJobOfferIdAndStatus(Long jobOfferId, ApplicationStatus status);

    boolean existsByApplicantIdAndJobOfferId(Long userId, Long jobOfferId);
}
