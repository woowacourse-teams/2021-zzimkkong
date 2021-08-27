package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.InputFieldException;
import org.springframework.http.HttpStatus;

public class ImpossibleEndTimeException extends InputFieldException {
    private static final String MESSAGE = "종료 시간을 확인해주세요.";

    public ImpossibleEndTimeException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, END_DATE_TIME);
    }
}
