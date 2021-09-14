package com.woowacourse.zzimkkong.exception.space;

import com.woowacourse.zzimkkong.exception.InputFieldException;
import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class TimeUnitInconsistencyException extends InputFieldException {
    private static final String MESSAGE = "최소, 최대 예약 가능 시간의 단위는 예약 시간 단위와 일치해야합니다";

    public TimeUnitInconsistencyException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, MINIMUM_MAXIMUM_TIME_UNIT);
    }
}
