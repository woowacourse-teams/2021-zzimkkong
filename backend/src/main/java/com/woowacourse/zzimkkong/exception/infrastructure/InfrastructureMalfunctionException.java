package com.woowacourse.zzimkkong.exception.infrastructure;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class InfrastructureMalfunctionException extends ZzimkkongException {
    public InfrastructureMalfunctionException(String message, HttpStatus status) {
        super(message, status);
    }

    public InfrastructureMalfunctionException(String message, Throwable cause, HttpStatus status) {
        super(message, cause, status);
    }
}
