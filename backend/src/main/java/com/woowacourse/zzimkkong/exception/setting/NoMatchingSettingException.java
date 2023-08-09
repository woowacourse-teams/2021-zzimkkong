package com.woowacourse.zzimkkong.exception.setting;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class NoMatchingSettingException extends ZzimkkongException {
    private static final String MESSAGE = "일치하는 설정이 존재하지 않습니다.";

    public NoMatchingSettingException() {
        super(MESSAGE, HttpStatus.NOT_FOUND);
    }
}
