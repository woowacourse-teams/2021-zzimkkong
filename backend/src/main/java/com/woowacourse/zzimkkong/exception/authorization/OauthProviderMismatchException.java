package com.woowacourse.zzimkkong.exception.authorization;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class OauthProviderMismatchException extends ZzimkkongException {
    private static final String MESSAGE = "소셜 로그인 제공자가 다릅니다. 다른 소셜 로그인 제공 업체를 통해 로그인하세요.";

    public OauthProviderMismatchException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }
}
