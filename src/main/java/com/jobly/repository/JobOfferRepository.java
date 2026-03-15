package com.jobly.repository;

import com.jobly.model.JobOfferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobOfferRepository extends JpaRepository<JobOfferEntity, Long> {

    List<JobOfferEntity> findAllByCreatorId(Long userId);

    @Query(value = """
            SELECT *
            FROM JOB_OFFERS
            WHERE LOWER(TITLE) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(COMPANY) LIKE LOWER(CONCAT('%', :search, '%'))
            LIMIT :limit OFFSET :offset
            """,
            nativeQuery = true)
    List<JobOfferEntity> findAllWithFilter(String search, int limit, int offset);

    @Query(value = """
            SELECT COUNT(*)
            FROM JOB_OFFERS
            WHERE LOWER(TITLE) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(COMPANY) LIKE LOWER(CONCAT('%', :search, '%'))
            """,
            nativeQuery = true)
    Integer countAllWithFilter(String search);

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
