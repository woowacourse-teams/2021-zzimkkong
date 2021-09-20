package com.woowacourse.s3proxy.exception;

import org.springframework.http.HttpStatus;

public class UnsupportedFileExtensionException extends S3ProxyException {
    private static final String MESSAGE = "지원하지 않는 확장자입니다.";

    public UnsupportedFileExtensionException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
