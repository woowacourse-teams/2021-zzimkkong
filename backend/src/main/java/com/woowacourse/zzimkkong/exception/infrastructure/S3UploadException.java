package com.woowacourse.zzimkkong.exception.infrastructure;

import org.springframework.http.HttpStatus;

public class S3UploadException extends InfrastructureException {
    private static final String MESSAGE = "이미지 버킷 업로드에 실패했습니다.";

    public S3UploadException(Exception exception) {
        super(MESSAGE, exception, HttpStatus.INTERNAL_SERVER_ERROR, MAP_IMAGE_SVG);
    }
}
