package com.jobly.dao;

import com.jobly.dto.PaginationAndFilterWrapper;
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

    public void delete(JobOfferEntity jobOffer) {
        jobOfferRepository.delete(jobOffer);
    }

    public List<JobOfferEntity> findAllWithPaginationAndFilter(PaginationAndFilterWrapper paginationAndFilterWrapper) {
        String search = paginationAndFilterWrapper.getSearch();
        Integer offset = paginationAndFilterWrapper.getOffset();
        Integer limit = paginationAndFilterWrapper.getLimit();

        int defaultOffset = offset != null ? offset : 0;
        int defaultLimit = limit != null ? limit : 10;
        String defaultSearch = search != null ? search : "";

        return jobOfferRepository.findAllWithFilter(defaultSearch, defaultLimit, defaultOffset);
    }

    public Integer countAllWithFilter(String search) {
        String defaultSearch = (search != null) ? search : "";
        return jobOfferRepository.countAllWithFilter(defaultSearch);
    }

    public List<JobOfferEntity> findByUserIdWithFilter(Long userId, PaginationAndFilterWrapper paginationAndFilterWrapper) {
        String search = paginationAndFilterWrapper.getSearch();
        Integer offset = paginationAndFilterWrapper.getOffset();
        Integer limit = paginationAndFilterWrapper.getLimit();

        int defaultOffset = offset != null ? offset : 0;
        int defaultLimit = limit != null ? limit : 10;
        String defaultSearch = search != null ? search : "";

        return jobOfferRepository.findAllByUserIdWithFilter(userId, defaultSearch, defaultLimit, defaultOffset);
    }

    public Integer countByUserIdWithFilter(Long userId, String search) {
        String defaultSearch = search != null ? search : "";
        return jobOfferRepository.countAllByUserIdWithFilter(userId, defaultSearch);
    }
}
