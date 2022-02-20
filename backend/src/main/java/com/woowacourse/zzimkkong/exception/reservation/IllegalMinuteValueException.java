package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class IllegalMinuteValueException extends ZzimkkongException {
    private static final String MESSAGE = "시간 단위 '분'은 5분 단위여야 합니다.";

    public IllegalMinuteValueException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
