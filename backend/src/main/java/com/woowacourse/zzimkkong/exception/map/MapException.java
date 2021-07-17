package com.woowacourse.zzimkkong.exception.map;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class MapException extends ZzimkkongException {
    public MapException(final String message, final HttpStatus status, final String field) {
        super(message, status, field);
    }
}
