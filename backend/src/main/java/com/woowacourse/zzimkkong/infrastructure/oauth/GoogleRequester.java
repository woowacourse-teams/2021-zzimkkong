package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OauthProvider;
import com.woowacourse.zzimkkong.domain.oauth.GoogleUserInfo;
import com.woowacourse.zzimkkong.domain.oauth.OauthUserInfo;
import com.woowacourse.zzimkkong.exception.infrastructure.oauth.UnableToGetTokenResponseFromGoogleException;
import com.woowacourse.zzimkkong.dto.member.oauth.GoogleTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Component
@PropertySource("classpath:config/oauth.properties")
public class GoogleRequester implements OauthAPIRequester {
    private final String clientId;
    private final String secretId;
    private final String redirectUri;
    private final String baseLoginUri;
    private final String baseUserUri;

    public GoogleRequester(
            @Value("${google.client-id}") final String clientId,
            @Value("${google.secret-id}") final String secretId,
            @Value("${google.uri.redirect}") final String redirectUri,
            @Value("${google.uri.oauth-login}") final String baseLoginUri,
            @Value("${google.uri.user-info}") final String baseUserUri) {
        this.clientId = clientId;
        this.secretId = secretId;
        this.redirectUri = redirectUri;
        this.baseLoginUri = baseLoginUri;
        this.baseUserUri = baseUserUri;
    }

    @Override
    public boolean supports(final OauthProvider oauthProvider) {
        return OauthProvider.GOOGLE.equals(oauthProvider);
    }

    @Override
    public OauthUserInfo getUserInfoByCode(final String code) {
        try {
            String token = getToken(code);
            return getUserInfo(token);
        } catch (RuntimeException e) {
            throw new UnableToGetTokenResponseFromGoogleException();
        }
    }

    private String getToken(final String code) {
        GoogleTokenResponse googleTokenResponse = WebClient.builder()
                .baseUrl(baseLoginUri)
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

    private GoogleUserInfo getUserInfo(final String token) {
        return WebClient.create()
                .get()
                .uri(baseUserUri)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(GoogleUserInfo.class)
                .blockOptional()
                .orElseThrow(UnableToGetTokenResponseFromGoogleException::new);
    }
}
