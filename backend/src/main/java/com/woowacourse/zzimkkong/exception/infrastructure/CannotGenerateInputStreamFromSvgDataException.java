package com.woowacourse.zzimkkong.exception.infrastructure;

import org.springframework.http.HttpStatus;

public class CannotGenerateInputStreamFromSvgDataException extends InfrastructureMalfunctionException {
    private static final String MESSAGE = "svg 데이터를 읽어올 수 없습니다.";

    public CannotGenerateInputStreamFromSvgDataException(Throwable throwable) {
        super(MESSAGE, throwable, HttpStatus.BAD_REQUEST);
    }
}
