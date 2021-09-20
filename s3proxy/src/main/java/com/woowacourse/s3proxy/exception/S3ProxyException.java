package com.woowacourse.s3proxy.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class S3ProxyException extends RuntimeException {
    protected final HttpStatus status;

    public S3ProxyException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public S3ProxyException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }
}
