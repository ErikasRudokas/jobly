package com.jobly.dto;

import com.jobly.gen.model.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyApplicationFilterWrapper {

    private ApplicationStatus status;

    private Integer offset;

    private Integer limit;
}
