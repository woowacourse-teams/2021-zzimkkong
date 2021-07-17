package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class ReservationException extends ZzimkkongException {
    public ReservationException(final String message, final HttpStatus status, final String field) {
        super(message, status, field);
    }
}
