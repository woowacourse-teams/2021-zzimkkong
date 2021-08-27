package com.woowacourse.zzimkkong.exception.map;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class NoSuchMapException extends ZzimkkongException {
    private static final String MESSAGE = "존재하지 않는 맵입니다.";

    public NoSuchMapException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
