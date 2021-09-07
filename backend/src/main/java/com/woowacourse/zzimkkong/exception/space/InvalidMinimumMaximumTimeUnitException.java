package com.woowacourse.zzimkkong.exception.space;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class InvalidMinimumMaximumTimeUnitException extends ZzimkkongException {
    private static final String MESSAGE = "최대 예약 가능시간은 최소 예약 가능시간보다 작을 수 없습니다";

    public InvalidMinimumMaximumTimeUnitException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
