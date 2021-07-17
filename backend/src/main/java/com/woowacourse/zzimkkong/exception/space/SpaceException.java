package com.woowacourse.zzimkkong.exception.space;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class SpaceException extends ZzimkkongException {
    public SpaceException(final String message, final HttpStatus status, final String field) {
        super(message, status, field);
    }
}
