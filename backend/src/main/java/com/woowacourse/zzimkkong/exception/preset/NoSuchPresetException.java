package com.woowacourse.zzimkkong.exception.preset;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class NoSuchPresetException extends ZzimkkongException {
    private static final String MESSAGE = "존재하지 않는 프리셋입니다.";

    public NoSuchPresetException() {
        super(MESSAGE, HttpStatus.NOT_FOUND);
    }
}
