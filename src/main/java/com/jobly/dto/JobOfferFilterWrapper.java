package com.jobly.dto;

import com.jobly.gen.model.WorkType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobOfferFilterWrapper {

    private String search;

    private Integer offset;

    private Integer limit;

    private Integer categoryId;

    private WorkType workType;

    private String location;

    private Integer salaryFrom;

    private Integer salaryTo;
}
