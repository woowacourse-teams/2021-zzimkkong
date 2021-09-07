package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OAuthProvider;
import com.woowacourse.zzimkkong.domain.oauth.GoogleUserInfo;
import com.woowacourse.zzimkkong.domain.oauth.OAuthUserInfo;
import com.woowacourse.zzimkkong.exception.infrastructure.oauth.UnableToGetTokenResponseFromGoogleException;
import com.woowacourse.zzimkkong.infrastructure.oauth.dto.GoogleTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Component
@PropertySource("classpath:config/oauth.properties")
public class GoogleRequester implements OAuthAPIRequester {
    private final String clientId;
    private final String secretId;
    private final String redirectUri;

    private GoogleRequester(
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
        GoogleTokenResponse googleTokenResponse = WebClient.builder()
                .baseUrl("https://www.googleapis.com/oauth2/v4/token")
                .build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("code", code)
                        .queryParam("client_id", clientId)
                        .queryParam("client_secret", secretId)
                        .queryParam("redirect_uri", redirectUri)
                        .queryParam("grant_type", "authorization_code")
                        .build())
                .headers(header -> {
                    header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                    header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                })
                .retrieve()
                .bodyToMono(GoogleTokenResponse.class)
                .blockOptional()
                .orElseThrow(UnableToGetTokenResponseFromGoogleException::new);
        return googleTokenResponse.getAccessToken();
    }

    private GoogleUserInfo getUserInfo(String token) {
        return WebClient.create()
                .get()
                .uri("https://www.googleapis.com/oauth2/v2/userinfo")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(GoogleUserInfo.class)
                .blockOptional()
                .orElseThrow(UnableToGetTokenResponseFromGoogleException::new);
    }
}
