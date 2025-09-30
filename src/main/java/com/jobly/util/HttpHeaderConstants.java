package com.jobly.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpHeaderConstants {

    public static final String CONTENT_DISPOSITION_ATTACHMENT = "attachment";
    public static final String CONTENT_DISPOSITION_INLINE = "inline";
}
