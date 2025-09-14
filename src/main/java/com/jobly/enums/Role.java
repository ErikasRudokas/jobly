package com.jobly.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum Role {
    USER("USER"),
    EMPLOYER("EMPLOYER"),
    ADMINISTRATOR("ADMINISTRATOR");

    private String value;
}
