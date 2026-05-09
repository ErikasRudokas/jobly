package com.jobly.mapper;

import com.jobly.dto.ApplicationFilterWrapper;
import com.jobly.dto.JobOfferFilterWrapper;
import com.jobly.dto.PaginationAndFilterWrapper;
import com.jobly.gen.model.ApplicationStatus;
import com.jobly.gen.model.WorkType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonMapper {

    public static PaginationAndFilterWrapper toPaginationAndFilterWrapper(String search, Integer offset, Integer limit) {
        return PaginationAndFilterWrapper.builder()
                .search(search)
                .offset(offset)
                .limit(limit)
                .build();
    }

    public static ApplicationFilterWrapper toApplicationFilterWrapper(ApplicationStatus status, Integer offset, Integer limit) {
        return ApplicationFilterWrapper.builder()
                .status(status)
                .offset(offset)
                .limit(limit)
                .build();
    }

    public static JobOfferFilterWrapper toJobOfferFilterWrapper(String search, Integer offset, Integer limit, Integer categoryId,
                                                                WorkType workType, String location, Integer salaryFrom, Integer salaryTo) {
        return JobOfferFilterWrapper.builder()
                .search(search)
                .offset(offset)
                .limit(limit)
                .categoryId(categoryId)
                .workType(workType)
                .location(location)
                .salaryFrom(salaryFrom)
                .salaryTo(salaryTo)
                .build();

    }
}
