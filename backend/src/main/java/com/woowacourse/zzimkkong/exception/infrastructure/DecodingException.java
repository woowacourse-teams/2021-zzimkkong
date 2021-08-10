package com.woowacourse.zzimkkong.exception.infrastructure;

import org.springframework.http.HttpStatus;

public class DecodingException extends InfrastructureMalfunctionException {
    private static final String MESSAGE = "링크 접근에 실패했습니다.";

    public DecodingException(final Throwable throwable) {
        super(MESSAGE, throwable, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
