package com.woowacourse.zzimkkong.exception.dto;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class EnabledDayOfWeekResponseReflectionException extends ZzimkkongException {
    private static final String MESSAGE = "EnabledDayOfWeek 필드를 reflection 할 수 없습니다.";

    public EnabledDayOfWeekResponseReflectionException() {
        super(MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
