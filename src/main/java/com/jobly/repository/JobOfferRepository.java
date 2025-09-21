package com.jobly.repository;

import com.jobly.model.JobOfferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobOfferRepository extends JpaRepository<JobOfferEntity, Long> {

    List<JobOfferEntity> findAllByCreatorId(Long userId);
}
