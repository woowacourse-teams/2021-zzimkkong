package com.woowacourse.s3proxy.exception;

import org.springframework.http.HttpStatus;

public class AuthorizationHeaderUninvolvedException extends S3ProxyException {
    private static final String MESSAGE = "인가에 실패했습니다.";
    public AuthorizationHeaderUninvolvedException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }
}
