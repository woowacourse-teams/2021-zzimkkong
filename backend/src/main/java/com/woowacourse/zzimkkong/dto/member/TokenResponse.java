package com.woowacourse.zzimkkong.dto.member;

public class TokenResponse {
    private String accessToken;

    public TokenResponse() {
    }

    public TokenResponse(final String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public static TokenResponse from(String token) {
        return new TokenResponse(token);
    }
}
