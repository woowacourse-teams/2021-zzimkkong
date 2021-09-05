package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OAuthProvider;
import com.woowacourse.zzimkkong.infrastructure.oauth.dto.GoogleTokenResponse;
import com.woowacourse.zzimkkong.infrastructure.oauth.dto.GoogleUserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GoogleRequester implements OAuthAPIRequester {
    private final String clientId;
    private final String secretId;
    private final String redirectUri;

    public GoogleRequester(
            @Value("${google.client-id}")
                    String clientId,
            @Value("${google.secret-id}")
                    String secretId,
            @Value("${google.redirect-uri}")
            String redirectUri) {
        this.clientId = clientId;
        this.secretId = secretId;
        this.redirectUri = redirectUri;
    }

    @Override
    public boolean supports(OAuthProvider oAuthProvider) {
        return OAuthProvider.GOOGLE.equals(oAuthProvider);
    }

    @Override
    public OAuthUserInfo getUserInfoByCode(String code) {
        String token = getToken(code);
        return getUserInfo(token);
    }

    private String getToken(String code) {
        GoogleTokenResponse googleTokenResponse = WebClient.create()
                .post()
                .uri(uri -> uri.path(OAuthProvider.GOOGLE.getTokenUri())
                        .queryParam("code", code)
                        .queryParam("client_id", clientId)
                        .queryParam("client_secret", secretId)
                        .queryParam("redirect_uri", redirectUri)
                        .queryParam("grant_type", "authorization_code")
                        .build())
                .retrieve()
                .bodyToMono(GoogleTokenResponse.class)
                .block();

        return googleTokenResponse.getAccessToken();
    }

    private OAuthUserInfo getUserInfo(String token) {
        return WebClient.create()
                .get()
                .uri(OAuthProvider.GOOGLE.getUserApi())
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(GoogleUserResponse.class)
                .block();
    }
}
