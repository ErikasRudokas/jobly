package com.jobly.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum TokenType {
    BEARER("Bearer"),
    REFRESH("Refresh");

    private String type;
}
