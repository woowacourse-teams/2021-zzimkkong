package com.woowacourse.zzimkkong.exception.space;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class NotEnoughAvailableTimeException extends ZzimkkongException {
    private static final String MESSAGE = "예약 가능한 시간이 최대 예약 가능 시간보다 작을 수 없습니다.";

    public NotEnoughAvailableTimeException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
