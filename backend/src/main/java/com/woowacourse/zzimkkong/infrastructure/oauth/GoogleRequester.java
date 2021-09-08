package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OAuthProvider;
import com.woowacourse.zzimkkong.domain.oauth.GoogleUserInfo;
import com.woowacourse.zzimkkong.domain.oauth.OAuthUserInfo;
import com.woowacourse.zzimkkong.exception.infrastructure.oauth.UnableToGetTokenResponseFromGoogleException;
import com.woowacourse.zzimkkong.infrastructure.oauth.dto.GoogleTokenResponse;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class GoogleRequester implements OAuthAPIRequester {
    private final String clientId;
    private final String secretId;
    private final String redirectUri;
    private final String baseLoginUri;
    private final String baseUserUri;

    public GoogleRequester(
            final String clientId,
            final String secretId,
            final String redirectUri,
            final String baseLoginUri,
            final String baseUserUri) {
        this.clientId = clientId;
        this.secretId = secretId;
        this.redirectUri = redirectUri;
        this.baseLoginUri = baseLoginUri;
        this.baseUserUri = baseUserUri;
    }

    @Override
    public boolean supports(final OAuthProvider oAuthProvider) {
        return OAuthProvider.GOOGLE.equals(oAuthProvider);
    }

    @Override
    public OAuthUserInfo getUserInfoByCode(final String code) {
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
