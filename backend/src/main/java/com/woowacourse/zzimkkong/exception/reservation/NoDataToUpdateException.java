package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.exception.ExceptionField;
import org.springframework.http.HttpStatus;

public class NoDataToUpdateException extends ReservationException {
    private static final String MESSAGE = "기존 예약 정보와 동일한 내용의 수정입니다.";

    public NoDataToUpdateException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, ExceptionField.START_DATETIME.fieldName());
    }
}
