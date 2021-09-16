package com.woowacourse.zzimkkong.exception.member;

import com.woowacourse.zzimkkong.domain.OauthProvider;
import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DuplicateEmailInOAuthFlowException extends ZzimkkongException {
    private static final String MESSAGE_FORMAT = "이미 등록된 이메일 주소입니다. %s 로그인을 이용해주세요.";

    public DuplicateEmailInOAuthFlowException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
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
