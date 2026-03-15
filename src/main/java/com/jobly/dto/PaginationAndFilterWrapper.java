package com.jobly.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginationAndFilterWrapper {

    private String search;

    private Integer offset;

    private Integer limit;
}
