package com.woowacourse.zzimkkong.exception.authorization;

import com.woowacourse.zzimkkong.domain.OauthProvider;
import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class OauthProviderMismatchException extends ZzimkkongException {
    private static final String MESSAGE_FORMAT = "소셜 로그인 제공자가 다릅니다. %s를 통해 로그인하세요.";

    public OauthProviderMismatchException(final String oauthProvider) {
        super(String.format(MESSAGE_FORMAT, oauthProvider), HttpStatus.UNAUTHORIZED);
    }

    public static OauthProviderMismatchException from(OauthProvider oauthProvider) {
        String message = formatMessage(oauthProvider);
        return new OauthProviderMismatchException(message);
    }

    private static String formatMessage(OauthProvider oauthProvider) {
        if (oauthProvider != null) {
            return String.format(MESSAGE_FORMAT, oauthProvider.name());
        }
        return String.format(MESSAGE_FORMAT, "이메일/비밀번호");
    }
}
