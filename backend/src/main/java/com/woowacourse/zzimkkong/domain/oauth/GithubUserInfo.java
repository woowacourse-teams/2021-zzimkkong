package com.woowacourse.zzimkkong.domain.oauth;

import com.woowacourse.zzimkkong.exception.infrastructure.oauth.NoPublicEmailHasBeenSetOnGithubException;

import java.util.Collections;
import java.util.Map;

public class GithubUserInfo implements OAuthUserInfo {
    private final Map<String, Object> info;

    private GithubUserInfo(final Map<String, Object> info) {
        this.info = Collections.unmodifiableMap(info);
    }

    public static OAuthUserInfo from(Map<String, Object> responseBody) {
        return new GithubUserInfo(responseBody);
    }

    @Override
    public String getEmail() {
        validatePublicEmailHasBeenSet();
        return (String) info.get("email");
    }

    private void validatePublicEmailHasBeenSet() {
        // todo Service 코드에 작성하기
        if (info.get("email") == null) {
            throw new NoPublicEmailHasBeenSetOnGithubException();
        }
    }
}
