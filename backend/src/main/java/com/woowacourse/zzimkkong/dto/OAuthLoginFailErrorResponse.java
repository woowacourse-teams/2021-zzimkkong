package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.exception.member.NoSuchOAuthMemberException;
import lombok.Getter;

@Getter
public class OAuthLoginFailErrorResponse extends ErrorResponse {
    private final String email;

    private OAuthLoginFailErrorResponse(String message, String email) {
        super(message);
        this.email = email;
    }

    public static OAuthLoginFailErrorResponse from(NoSuchOAuthMemberException exception) {
        String email = exception.getEmail();
        return new OAuthLoginFailErrorResponse(exception.getMessage(), email);
    }
}
