package com.woowacourse.zzimkkong.exception.infrastructure.oauth;

import com.woowacourse.zzimkkong.exception.infrastructure.InfrastructureMalfunctionException;
import org.springframework.http.HttpStatus;

public class ErrorResponseToGetGithubAccessTokenException extends InfrastructureMalfunctionException {
    private static final String MESSAGE = "소셜 로그인에 실패했습니다. 다시 시도해주세요.";

    public ErrorResponseToGetGithubAccessTokenException(String errorMessageFromGithub) {
        super(MESSAGE, new Throwable(errorMessageFromGithub), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
