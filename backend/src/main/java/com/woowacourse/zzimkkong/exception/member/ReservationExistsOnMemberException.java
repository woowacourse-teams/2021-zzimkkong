package com.woowacourse.zzimkkong.exception.member;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class ReservationExistsOnMemberException extends ZzimkkongException {
    private static final String MESSAGE = "예약이 존재하는 공간이 있습니다. 사전에 미리 취소해주세요.";

    public ReservationExistsOnMemberException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
