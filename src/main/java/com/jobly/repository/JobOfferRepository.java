package com.jobly.repository;

import com.jobly.model.JobOfferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobOfferRepository extends JpaRepository<JobOfferEntity, Long> {

    @Query(value = """
             SELECT *
                FROM JOB_OFFERS
                WHERE (:search IS NULL OR LOWER(TITLE) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(COMPANY) LIKE LOWER(CONCAT('%', :search, '%')))
                  AND (:categoryId IS NULL OR CATEGORY_ID = :categoryId)
                  AND (:workType IS NULL OR WORK_TYPE = :workType)
                  AND (:location IS NULL OR LOWER(LOCATION) LIKE LOWER(CONCAT('%', :location, '%')))
                  AND (:salaryFrom IS NULL OR SALARY >= :salaryFrom)
                  AND (:salaryTo IS NULL OR SALARY <= :salaryTo)
                ORDER BY CREATED_AT DESC
                LIMIT :limit OFFSET :offset
            """,
            nativeQuery = true)
    List<JobOfferEntity> findAllWithFilter(String search, Integer categoryId, String workType, String location, Integer salaryFrom, Integer salaryTo, int limit, int offset);

    @Query(value = """
            SELECT COUNT(*)
            FROM JOB_OFFERS
            WHERE (:search IS NULL OR LOWER(TITLE) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(COMPANY) LIKE LOWER(CONCAT('%', :search, '%')))
                  AND (:categoryId IS NULL OR CATEGORY_ID = :categoryId)
                  AND (:workType IS NULL OR WORK_TYPE = :workType)
                  AND (:location IS NULL OR LOWER(LOCATION) LIKE LOWER(CONCAT('%', :location, '%')))
                  AND (:salaryFrom IS NULL OR SALARY >= :salaryFrom)
                  AND (:salaryTo IS NULL OR SALARY <= :salaryTo)
            """,
            nativeQuery = true)
    Integer countAllWithFilter(String search, Integer categoryId, String workType, String location, Integer salaryFrom, Integer salaryTo);

    @Query(value = """
            SELECT *
            FROM JOB_OFFERS
            WHERE CREATOR_ID = :userId AND LOWER(TITLE) LIKE LOWER(CONCAT('%', :search, '%'))
            LIMIT :limit OFFSET :offset
            """,
            nativeQuery = true)
    List<JobOfferEntity> findAllByUserIdWithFilter(Long userId, String search, int limit, int offset);

    @Query(value = """
            SELECT COUNT(*)
            FROM JOB_OFFERS
            WHERE CREATOR_ID = :userId AND LOWER(TITLE) LIKE LOWER(CONCAT('%', :search, '%'))
            """,
            nativeQuery = true)
    Integer countAllByUserIdWithFilter(Long userId, String search);
}
