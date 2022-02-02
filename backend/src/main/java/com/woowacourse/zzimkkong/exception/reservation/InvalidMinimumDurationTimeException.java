package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class InvalidMinimumDurationTimeException extends ZzimkkongException {
    private static final String MESSAGE = "최소 예약가능시간을 확인해주세요.";

    public InvalidMinimumDurationTimeException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
