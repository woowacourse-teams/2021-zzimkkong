package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.InputFieldException;
import org.springframework.http.HttpStatus;

public class ImpossibleStartTimeException extends InputFieldException {
    private static final String MESSAGE = "시작 시간을 확인해주세요.";

    public ImpossibleStartTimeException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, START_DATE_TIME);
    }
}
