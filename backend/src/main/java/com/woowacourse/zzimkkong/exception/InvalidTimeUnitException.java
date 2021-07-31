package com.woowacourse.zzimkkong.exception;

import org.springframework.http.HttpStatus;

public class InvalidTimeUnitException extends ZzimkkongException{
    public InvalidTimeUnitException() {
        super("상위 메시지를 데려올거에요", HttpStatus.BAD_REQUEST, "setting");
    }
}
