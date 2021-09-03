package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OAuthProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:config/oauth.properties")
public class GithubRequester implements OAuthAPIRequester {
    private final String clientId;
    private final String secretId;

    public GithubRequester(
            @Value("${github.client-id}")
                    String clientId,
            @Value("${github.secret-id}")
                    String secretId) {
        this.clientId = clientId;
        this.secretId = secretId;
    }

    @Override
    public boolean supports(OAuthProvider oAuthProvider) {
        return OAuthProvider.GITHUB.equals(oAuthProvider);
    }

    @Override
    public OAuthUserInfo getUserInfoByCode(String code) {
        String token = getToken(code);
        return getUserInfo(token);
    }

    private OAuthUserInfo getUserInfo(String token) {
        // todo 유저 정보 조회 Open API 요청 및 parse
        return null;
    }

    private String getToken(String code) {
        // todo AccessToken 발급 요청 및 parse
        return null;
    }
}
