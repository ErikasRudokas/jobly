package com.jobly.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {

    public static OffsetDateTime getCurrentUtcTime() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }
}
