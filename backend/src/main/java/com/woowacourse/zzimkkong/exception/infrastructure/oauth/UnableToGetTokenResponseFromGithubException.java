package com.woowacourse.zzimkkong.exception.infrastructure.oauth;

import com.woowacourse.zzimkkong.exception.infrastructure.InfrastructureMalfunctionException;
import org.springframework.http.HttpStatus;

public class UnableToGetTokenResponseFromGithubException extends InfrastructureMalfunctionException {
    private static final String MESSAGE = "소셜 로그인에 실패했습니다.";

    public UnableToGetTokenResponseFromGithubException() {
        super(MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
