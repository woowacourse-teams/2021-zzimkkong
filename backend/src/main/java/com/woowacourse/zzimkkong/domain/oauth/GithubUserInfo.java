package com.woowacourse.zzimkkong.domain.oauth;

import com.woowacourse.zzimkkong.exception.infrastructure.oauth.NoPublicEmailOnGithubException;

import java.util.Collections;
import java.util.Map;

public class GithubUserInfo implements OauthUserInfo {
    private final Map<String, Object> info;

    private GithubUserInfo(final Map<String, Object> info) {
        this.info = Collections.unmodifiableMap(info);
    }

    public static OauthUserInfo from(Map<String, Object> responseBody) {
        return new GithubUserInfo(responseBody);
    }

    @Override
    public String getEmail() {
        validatePublicEmailHasBeenSet();
        return (String) info.get("email");
    }

    private void validatePublicEmailHasBeenSet() {
        if (info.get("email") == null) {
            throw new NoPublicEmailOnGithubException();
        }
    }
}
