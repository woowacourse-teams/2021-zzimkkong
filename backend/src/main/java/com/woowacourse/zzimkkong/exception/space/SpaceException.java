package com.woowacourse.zzimkkong.exception.space;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class SpaceException extends ZzimkkongException {
    public static final String SPACE_ID = "spaceId";
    public static final String DAY_OF_WEEK = "dayOfWeek";

    public SpaceException(final String message, final HttpStatus status, final String field) {
        super(message, status, field);
    }
}
