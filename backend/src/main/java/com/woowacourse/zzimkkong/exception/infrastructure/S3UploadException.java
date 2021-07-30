package com.woowacourse.zzimkkong.exception.infrastructure;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class S3UploadException extends ZzimkkongException {
    private static final String MAP_IMAGE = "mapImage";
    private static final String MESSAGE = "이미지 버킷 업로드에 실패했습니다.";

    public S3UploadException(Exception exception) {
        super(MESSAGE, exception, HttpStatus.INTERNAL_SERVER_ERROR, MAP_IMAGE);
    }
}
