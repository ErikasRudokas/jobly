package com.jobly.dao;

import com.jobly.exception.general.NotFoundException;
import com.jobly.model.JobOfferEntity;
import com.jobly.repository.JobOfferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobOfferDao {

    private final JobOfferRepository jobOfferRepository;

    public JobOfferEntity findById(Long id) {
        return jobOfferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job offer not found with id: " + id));
    }

    public JobOfferEntity save(JobOfferEntity jobOfferEntity) {
        return jobOfferRepository.save(jobOfferEntity);
    }

    public List<JobOfferEntity> findAll() {
        return jobOfferRepository.findAll();
    }

    public List<JobOfferEntity> findByUserId(Long userId) {
        return jobOfferRepository.findAllByCreatorId(userId);
    }

    public void delete(JobOfferEntity jobOffer) {
        jobOfferRepository.delete(jobOffer);
    }
}
