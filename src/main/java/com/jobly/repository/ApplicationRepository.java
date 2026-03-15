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

    Integer countAllByJobOfferIdAndStatusIn(Long jobOfferId, List<ApplicationStatus> statuses);

    boolean existsByApplicantIdAndJobOfferIdAndStatusIn(Long userId, Long jobOfferId, List<ApplicationStatus> statuses);

    List<ApplicationEntity> findAllByJobOfferIdAndStatusIn(Long jobOfferId, List<ApplicationStatus> statuses);

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

    @Query(value = """
        SELECT *
        FROM APPLICATIONS
        WHERE JOB_OFFER_ID = :jobOfferId
        AND STATUS IN (:statuses)
        ORDER BY CREATED_AT DESC
        LIMIT :limit OFFSET :offset
        """,
    nativeQuery = true)
    List<ApplicationEntity> findAllByJobOfferIdAndFilter(Long jobOfferId, List<String> statuses, Integer limit, Integer offset);

    @Query(value = """
        SELECT COUNT(*)
        FROM APPLICATIONS
        WHERE JOB_OFFER_ID = :jobOfferId
        AND STATUS IN (:statuses)
        """,
        nativeQuery = true)
    Integer countAllByJobOfferIdAndFilter(Long jobOfferId, List<String> statuses);
}
