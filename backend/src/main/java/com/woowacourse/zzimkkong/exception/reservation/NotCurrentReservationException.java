package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class NotCurrentReservationException extends ZzimkkongException {
    private static final String MESSAGE = "사용중인 예약에 대해서만 조기종료가 가능합니다.";

    public NotCurrentReservationException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
