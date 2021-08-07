package com.woowacourse.zzimkkong.exception.preset;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class NoAuthorityOnPresetException extends ZzimkkongException {
    private static final String MESSAGE = "해당 프리셋에 대한 권한이 없습니다.";

    public NoAuthorityOnPresetException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED, "member");
    }
}

