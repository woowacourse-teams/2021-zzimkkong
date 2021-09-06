package com.woowacourse.zzimkkong.exception.infrastructure.oauth;

import com.woowacourse.zzimkkong.exception.infrastructure.InfrastructureMalfunctionException;
import org.springframework.http.HttpStatus;

public class NoPublicEmailHasBeenSetOnGithubException extends InfrastructureMalfunctionException {
    private static final String MESSAGE = "소셜 로그인에 실패했습니다. 깃허브의 Public Email(Setting -> Profile)을 설정해주시기 바랍니다.";

    public NoPublicEmailHasBeenSetOnGithubException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
