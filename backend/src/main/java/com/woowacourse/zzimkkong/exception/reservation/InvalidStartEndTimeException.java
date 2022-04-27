package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class InvalidStartEndTimeException extends ZzimkkongException {
    private static final String MESSAGE = "공간의 예약가능 시간을 확인해주세요.";

    public InvalidStartEndTimeException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
