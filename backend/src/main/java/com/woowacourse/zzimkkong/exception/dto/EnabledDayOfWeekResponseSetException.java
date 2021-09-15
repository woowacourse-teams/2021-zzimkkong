package com.woowacourse.zzimkkong.exception.dto;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class EnabledDayOfWeekResponseSetException extends ZzimkkongException {
    private static final String MESSAGE = "EnabledDayOfWeekResponse에 요일을 세팅할 수 없습니다. 요일 input을 확인해주세요";

    public EnabledDayOfWeekResponseSetException() {
        super(MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
