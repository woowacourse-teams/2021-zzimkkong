package com.woowacourse.s3proxy.exception;

import org.springframework.http.HttpStatus;

public class S3UploadException extends S3ProxyException {
    private static final String MESSAGE = "이미지 버킷 업로드에 실패했습니다.";

    public S3UploadException(final Throwable cause) {
        super(MESSAGE, cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

