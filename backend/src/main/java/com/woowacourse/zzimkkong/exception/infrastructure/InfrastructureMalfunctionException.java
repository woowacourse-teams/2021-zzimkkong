package com.woowacourse.zzimkkong.exception.infrastructure;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class InfrastructureMalfunctionException extends ZzimkkongException {
    public InfrastructureMalfunctionException(final String message, final HttpStatus status) {
        super(message, status);
    }

    public InfrastructureMalfunctionException(final String message, final Throwable cause, final HttpStatus status) {
        super(message, cause, status);
    }
}
