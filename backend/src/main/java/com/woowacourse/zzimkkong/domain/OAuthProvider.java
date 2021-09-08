package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.infrastructure.UnsupportedOAuthProviderException;

import java.util.Arrays;

public enum OAuthProvider {
    GITHUB, GOOGLE;

    public static OAuthProvider valueOfWithIgnoreCase(String value) {
        return Arrays.stream(values())
                .filter(oAuthProvider -> oAuthProvider.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(UnsupportedOAuthProviderException::new);
    }
}
