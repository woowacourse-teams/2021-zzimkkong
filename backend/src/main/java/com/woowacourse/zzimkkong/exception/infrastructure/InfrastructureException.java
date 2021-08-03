package com.woowacourse.zzimkkong.exception.infrastructure;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class InfrastructureException extends ZzimkkongException {
    public InfrastructureException(String message, Throwable cause, HttpStatus status, String field) {
        super(message, cause, status, field);
    }
}
