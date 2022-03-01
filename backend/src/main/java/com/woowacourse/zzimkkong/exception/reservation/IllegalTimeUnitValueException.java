package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class IllegalTimeUnitValueException extends ZzimkkongException {
    public static final String MESSAGE_FORMAT = "'%d'분은 허용되지 않는 시간 단위 입니다";

    public IllegalTimeUnitValueException(final int minutes) {
        super(String.format(MESSAGE_FORMAT, minutes), HttpStatus.BAD_REQUEST);
    }
}
