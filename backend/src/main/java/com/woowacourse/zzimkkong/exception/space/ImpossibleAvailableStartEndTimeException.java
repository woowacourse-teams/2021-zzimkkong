package com.woowacourse.zzimkkong.exception.space;

import com.woowacourse.zzimkkong.exception.InputFieldException;
import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class ImpossibleAvailableStartEndTimeException extends InputFieldException {
    private static final String MESSAGE = "예약이 닫힐 시간은 예약이 열릴 시간보다 이전일 수 없습니다.";

    public ImpossibleAvailableStartEndTimeException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST, AVAILABLE_START_END_TIME);
    }
}
