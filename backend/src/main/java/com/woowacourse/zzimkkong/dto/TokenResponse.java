package com.woowacourse.zzimkkong.dto;

public class TokenResponse {
    private String accessToken;

    public TokenResponse() {
    }

    public TokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public static TokenResponse of(String token) {
        return new TokenResponse(token);
    }
}
