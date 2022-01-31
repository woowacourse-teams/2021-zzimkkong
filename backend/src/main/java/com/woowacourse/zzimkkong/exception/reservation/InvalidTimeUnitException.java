package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class InvalidTimeUnitException extends ZzimkkongException {
    private static final String MESSAGE = "예약 시간단위를 확인해주세요.";

    public InvalidTimeUnitException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
