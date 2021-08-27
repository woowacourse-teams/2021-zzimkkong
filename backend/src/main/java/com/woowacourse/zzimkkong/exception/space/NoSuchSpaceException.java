package com.woowacourse.zzimkkong.exception.space;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class NoSuchSpaceException extends ZzimkkongException {
    private static final String MESSAGE = "존재하지 않는 공간입니다.";

    public NoSuchSpaceException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
