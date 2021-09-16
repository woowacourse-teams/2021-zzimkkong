package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.exception.member.NoSuchOAuthMemberException;
import lombok.Getter;

@Getter
public class OAuthLoginFailResponse extends ErrorResponse {
    private final String email;

    public OAuthLoginFailResponse(String message, String email) {
        super(message);
        this.email = email;
    }

    public static OAuthLoginFailResponse from(NoSuchOAuthMemberException exception) {
        String email = exception.getEmail();
        return new OAuthLoginFailResponse(exception.getMessage(), email);
    }
}
