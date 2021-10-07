package com.woowacourse.zzimkkong.exception.infrastructure;

import org.springframework.http.HttpStatus;

public class S3UploadException extends InfrastructureMalfunctionException {
    private static final String MESSAGE = "이미지 버킷 업로드에 실패했습니다.";

    public S3UploadException() {
        super(MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public S3UploadException(final Exception exception) {
        super(MESSAGE, exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
