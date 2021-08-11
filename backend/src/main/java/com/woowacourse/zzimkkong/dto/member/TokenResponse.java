package com.woowacourse.zzimkkong.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenResponse {
    private String accessToken;

    public TokenResponse(final String accessToken) {
        this.accessToken = accessToken;
    }

    public static TokenResponse from(final String token) {
        return new TokenResponse(token);
    }
}
