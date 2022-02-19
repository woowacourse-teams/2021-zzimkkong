package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class MinusMinuteValueException extends ZzimkkongException {
    private static final String MESSAGE = "시간 단위 '분'은 음수일 수 없습니다";

    public MinusMinuteValueException() {
        super(MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
