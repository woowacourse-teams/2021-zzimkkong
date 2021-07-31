package com.woowacourse.zzimkkong.exception;

import org.springframework.http.HttpStatus;

public class InvalidConferenceTimeException extends ZzimkkongException {
    public InvalidConferenceTimeException() {
        super("상위 메시지를 데려올거에요", HttpStatus.BAD_REQUEST, "setting");
    }
}
