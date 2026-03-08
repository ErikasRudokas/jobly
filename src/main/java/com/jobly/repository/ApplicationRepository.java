package com.jobly.repository;

import com.jobly.gen.model.ApplicationStatus;
import com.jobly.model.ApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {

    Optional<ApplicationEntity> findByApplicantIdAndId(Long applicantId, Long id);

    Integer countAllByJobOfferIdAndStatus(Long jobOfferId, ApplicationStatus status);

    Integer countAllByJobOfferId(Long jobOfferId);

    boolean existsByApplicantIdAndJobOfferIdAndStatusIn(Long userId, Long jobOfferId, List<ApplicationStatus> statuses);

    List<ApplicationEntity> findAllByJobOfferId(Long jobOfferId);

    @Query(value = """
        SELECT *
        FROM APPLICATIONS
        WHERE APPLICANT_ID = :userId
        AND STATUS IN (:statuses)
        ORDER BY CREATED_AT DESC
        LIMIT :limit OFFSET :offset
        """,
    nativeQuery = true)
    List<ApplicationEntity> findAllByUserIdAndFilter(Long userId, List<String> statuses, Integer limit, Integer offset);

    @Query(value = """
        SELECT COUNT(*)
        FROM APPLICATIONS
        WHERE APPLICANT_ID = :userId
        AND STATUS IN (:statuses)
        """,
    nativeQuery = true)
    Integer countAllByUserIdAndFilter(Long userId, List<String> statuses);
}
