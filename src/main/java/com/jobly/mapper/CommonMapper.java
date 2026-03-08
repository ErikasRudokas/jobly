package com.jobly.mapper;

import com.jobly.dto.PaginationAndFilterWrapper;
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
}
