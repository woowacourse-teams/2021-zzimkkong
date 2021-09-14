package com.woowacourse.zzimkkong.domain.oauth;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Map;

@Getter
@NoArgsConstructor
public class GoogleUserInfo implements OauthUserInfo {
    private Map<String, Object> info;

    private GoogleUserInfo(final Map<String, Object> info) {
        this.info = Collections.unmodifiableMap(info);
    }

    public static GoogleUserInfo from(final Map<String, Object> responseBody) {
        return new GoogleUserInfo(responseBody);
    }

    @Override
    public String getEmail() {
        return (String) info.get("email");
    }
}

