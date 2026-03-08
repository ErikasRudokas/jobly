package com.jobly.mapper;

import com.jobly.dto.MyApplicationFilterWrapper;
import com.jobly.dto.PaginationAndFilterWrapper;
import com.jobly.gen.model.ApplicationStatus;
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

    public static MyApplicationFilterWrapper toMyApplicationFilterWrapper(ApplicationStatus status, Integer offset, Integer limit) {
        return MyApplicationFilterWrapper.builder()
                .status(status)
                .offset(offset)
                .limit(limit)
                .build();
    }
}
