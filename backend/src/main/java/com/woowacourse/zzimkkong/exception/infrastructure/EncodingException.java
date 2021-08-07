package com.woowacourse.zzimkkong.exception.infrastructure;

import org.springframework.http.HttpStatus;

public class EncodingException extends InfrastructureException {
    private static final String MESSAGE = "링크 생성에 실패했습니다.";

    public EncodingException(Throwable throwable) {
        super(MESSAGE, throwable, HttpStatus.INTERNAL_SERVER_ERROR, PUBLIC_MAP_ID);
    }
}
