package com.jobly.service.api;

import com.jobly.gen.api.parser.CvParserApi;
import com.jobly.gen.api.parser.data.CvParseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.AbstractResource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CvParserApiService {

    private final CvParserApi cvParserApi;

    public CvParseResponse parseCv(AbstractResource cvFile) {
        var response = cvParserApi.parseCvPost(cvFile).block();
        log.info("CV parsed successfully: {}", response);
        return response;
    }
}
