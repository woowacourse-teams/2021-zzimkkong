package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class InvalidMinimumDurationTimeInEarlyStopException extends ZzimkkongException {

    private static final String MESSAGE = "조기 종료는 최소 5분 이후부터 가능합니다.";

    public InvalidMinimumDurationTimeInEarlyStopException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
