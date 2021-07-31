package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class ReservationException extends ZzimkkongException {
    public static final String RESERVATION_ID = "reservationId";
    public static final String START_DATE_TIME = "startDateTime";
    public static final String END_DATE_TIME = "endDateTime";
    public static final String RESERVATION_PASSWORD = "password";
    public static final String SETTING = "setting";

    public ReservationException(final String message, final HttpStatus status, final String field) {
        super(message, status, field);
    }
}
