package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.exception.infrastructure.UnsupportedOauthProviderException;

import java.util.Arrays;

public enum OauthProvider {
    GITHUB, GOOGLE;

    public static OauthProvider valueOfWithIgnoreCase(String value) {
        return Arrays.stream(values())
                .filter(oauthProvider -> oauthProvider.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(UnsupportedOauthProviderException::new);
    }

    public boolean isSameAs(OauthProvider oauthProvider) {
        return this.equals(oauthProvider);
    }
}
