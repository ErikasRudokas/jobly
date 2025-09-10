package com.jobly.handler;

import com.jobly.gen.api.TempApiDelegate;
import com.jobly.gen.model.TempGet200Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TempApiHandler implements TempApiDelegate {

    @Override
    public ResponseEntity<TempGet200Response> tempGet() {
        return ResponseEntity.ok(new TempGet200Response().message("This is a temporary endpoint"));
    }
}
