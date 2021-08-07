package com.woowacourse.zzimkkong.exception.infrastructure;

import org.springframework.http.HttpStatus;

public class SharingIdException extends InfrastructureException {
    public static final String PUBLIC_ID = "publicId";

    public SharingIdException(String message, HttpStatus httpStatus) {
        super(message, httpStatus, PUBLIC_ID);
    }

    public SharingIdException(String message, Throwable throwable, HttpStatus httpStatus) {
        super(message, throwable, httpStatus, PUBLIC_ID);
    }
}
