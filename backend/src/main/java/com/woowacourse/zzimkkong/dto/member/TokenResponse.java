package com.woowacourse.zzimkkong.dto.member;

import lombok.Getter;

@Getter
public class TokenResponse {
    private String accessToken;

    public TokenResponse() {
    }

    public TokenResponse(final String accessToken) {
        this.accessToken = accessToken;
    }

    public static TokenResponse from(final String token) {
        return new TokenResponse(token);
    }
}
