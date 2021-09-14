package com.woowacourse.zzimkkong.exception.space;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class NoSuchDayOfWeekException extends ZzimkkongException {
    private static final String MESSAGE = "존재하지 않는 요일입니다.";

    public NoSuchDayOfWeekException() {
        super(MESSAGE, HttpStatus.NOT_FOUND);
    }
}
