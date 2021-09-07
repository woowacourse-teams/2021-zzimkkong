package com.woowacourse.zzimkkong.exception.space;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class TimeUnitMismatchException extends ZzimkkongException {
    private static final String MESSAGE = "예약이 열릴 시간과 닫힐 시간은 예약 시간 단위와 맞아야 합니다";

    public TimeUnitMismatchException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
