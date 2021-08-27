package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class NoSuchReservationException extends ZzimkkongException {
    private static final String MESSAGE = "존재하지 않는 예약입니다.";

    public NoSuchReservationException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
