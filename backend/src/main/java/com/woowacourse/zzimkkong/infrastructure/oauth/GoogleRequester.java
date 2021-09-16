package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OauthProvider;
import com.woowacourse.zzimkkong.domain.oauth.GoogleUserInfo;
import com.woowacourse.zzimkkong.domain.oauth.OauthUserInfo;
import com.woowacourse.zzimkkong.exception.infrastructure.oauth.ErrorResponseToGetGithubAccessTokenException;
import com.woowacourse.zzimkkong.exception.infrastructure.oauth.UnableToGetTokenResponseFromGoogleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

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
        return oauthProvider.isSameAs(OauthProvider.GOOGLE);
    }

    @Override
    public OauthUserInfo getUserInfoByCode(final String code) {
        String token = getToken(code);
        return getUserInfo(token);
    }

    private String getToken(final String code) {
        Map<String, Object> responseBody = googleOauthLoginClient()
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
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .blockOptional()
                .orElseThrow(UnableToGetTokenResponseFromGoogleException::new);
        validateResponseBody(responseBody);
        return responseBody.get("access_token").toString();
    }

    private void validateResponseBody(Map<String, Object> responseBody) {
        if (!responseBody.containsKey("access_token")) {
            throw new ErrorResponseToGetGithubAccessTokenException(responseBody.get("error_description").toString());
        }
    }

    private GoogleUserInfo getUserInfo(final String token) {
        Map<String, Object> responseBody = googleUserClient()
                .get()
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .blockOptional()
                .orElseThrow(UnableToGetTokenResponseFromGoogleException::new);

        return GoogleUserInfo.from(responseBody);
    }

    private WebClient googleOauthLoginClient() {
        return WebClient.builder()
                .baseUrl(baseLoginUri)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private WebClient googleUserClient() {
        return WebClient.builder()
                .baseUrl(baseUserUri)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
