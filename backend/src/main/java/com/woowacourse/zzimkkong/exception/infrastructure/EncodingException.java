package com.woowacourse.zzimkkong.exception.infrastructure;

import org.springframework.http.HttpStatus;

public class EncodingException extends InfrastructureException {
    private static final String MESSAGE = "접근을 위한 키의 인코딩에 실패했습니다. 관리자에게 문의하세요.";

    public EncodingException(Throwable throwable) {
        super(MESSAGE, throwable, HttpStatus.INTERNAL_SERVER_ERROR, "publicMapId");
    }
}
