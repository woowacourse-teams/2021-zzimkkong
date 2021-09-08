package com.woowacourse.zzimkkong.exception.infrastructure;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class UnsupportedOauthProviderException extends ZzimkkongException {
    private static final String MESSAGE = "지원되지 않는 Oauth 제공자입니다.";

    public UnsupportedOauthProviderException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
