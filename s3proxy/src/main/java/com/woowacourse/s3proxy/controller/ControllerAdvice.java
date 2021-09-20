package com.woowacourse.s3proxy.controller;

import com.woowacourse.s3proxy.dto.ErrorResponse;
import com.woowacourse.s3proxy.exception.S3ProxyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(S3ProxyException.class)
    public ResponseEntity<ErrorResponse> zzimkkongExceptionHandler(final S3ProxyException exception) {
        log.info(exception.getMessage());
        return ResponseEntity
                .status(exception.getStatus())
                .body(ErrorResponse.from(exception));
    }
}
