package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class InvalidReservationEnableException extends ZzimkkongException {
    private static final String MESSAGE = "현재 예약이 불가능한 공간입니다.";

    public InvalidReservationEnableException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
