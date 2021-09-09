package com.woowacourse.zzimkkong.exception.space;

import com.woowacourse.zzimkkong.exception.InputFieldException;
import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class NotEnoughAvailableTimeException extends InputFieldException {
    private static final String MESSAGE = "예약 가능한 시간의 범위가 최대 예약 가능 시간보다 작을 수 없습니다.";

    public NotEnoughAvailableTimeException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, AVAILABLE_START_END_TIME);
    }
}
