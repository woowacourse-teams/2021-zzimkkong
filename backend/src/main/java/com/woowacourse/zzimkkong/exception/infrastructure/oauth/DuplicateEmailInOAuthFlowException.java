package com.woowacourse.zzimkkong.exception.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OauthProvider;
import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class DuplicateEmailInOAuthFlowException extends ZzimkkongException {
    private static final String MESSAGE_FORMAT = "이미 가입된 회원입니다. %s를 통해 로그인하세요.";

    public DuplicateEmailInOAuthFlowException(final String oauthProvider) {
        super(String.format(MESSAGE_FORMAT, oauthProvider), HttpStatus.BAD_REQUEST);
    }

    public static DuplicateEmailInOAuthFlowException from(OauthProvider oauthProvider) {
        String message = formatMessage(oauthProvider);
        return new DuplicateEmailInOAuthFlowException(message);
    }

    private static String formatMessage(OauthProvider oauthProvider) {
        if (oauthProvider != null) {
            return String.format(MESSAGE_FORMAT, oauthProvider.name());
        }
        return String.format(MESSAGE_FORMAT, "이메일/비밀번호");
    }
}
