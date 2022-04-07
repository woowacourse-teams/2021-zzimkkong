package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime;
import com.woowacourse.zzimkkong.domain.OauthProvider;
import com.woowacourse.zzimkkong.domain.oauth.GoogleUserInfo;
import com.woowacourse.zzimkkong.domain.oauth.OauthUserInfo;
import com.woowacourse.zzimkkong.exception.infrastructure.oauth.ErrorResponseToGetAccessTokenException;
import com.woowacourse.zzimkkong.exception.infrastructure.oauth.UnableToGetTokenResponseFromGoogleException;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component
@PropertySource("classpath:config/oauth.properties")
@LogMethodExecutionTime(group = "infrastructure")
public class GoogleRequester implements OauthAPIRequester {
    private final String clientId;
    private final String secretId;
    private final String redirectUri;
    private final String baseLoginUri;
    private final String baseUserUri;
    private final WebClient googleOauthLoginClient;
    private final WebClient googleUserClient;

    public GoogleRequester(
            @Value("${google.client-id}") final String clientId,
            @Value("${google.secret-id}") final String secretId,
            @Value("${google.uri.redirect}") final String redirectUri,
            @Value("${google.uri.oauth-login}") final String baseLoginUri,
            @Value("${google.uri.user-info}") final String baseUserUri,
            final WebClient webClient) {
        this.clientId = clientId;
        this.secretId = secretId;
        this.redirectUri = redirectUri;
        this.baseLoginUri = baseLoginUri;
        this.baseUserUri = baseUserUri;
        this.googleOauthLoginClient = googleOauthLoginClient(webClient);
        this.googleUserClient = googleUserClient(webClient);
    }

    @Override
    public boolean supports(final OauthProvider oauthProvider) {
        return oauthProvider.isSameAs(OauthProvider.GOOGLE);
    }

    @Override
    public OauthUserInfo getUserInfoByCode(final String code) {
        log.error("google client id: {}", clientId);
        log.error("google secret id: {}", secretId);
        log.error("google redirect url: {}", redirectUri);

        String token = getToken(code);
        return getUserInfo(token);
    }

    private String getToken(final String code) {
        Map<String, Object> responseBody = googleOauthLoginClient
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
            throw new ErrorResponseToGetAccessTokenException(responseBody.get("error_description").toString());
        }
    }

    private GoogleUserInfo getUserInfo(final String token) {
        Map<String, Object> responseBody = googleUserClient
                .get()
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .blockOptional()
                .orElseThrow(UnableToGetTokenResponseFromGoogleException::new);

        return GoogleUserInfo.from(responseBody);
    }

    private WebClient googleOauthLoginClient(final WebClient webClient) {
        return webClient.mutate()
                .baseUrl(baseLoginUri)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private WebClient googleUserClient(final WebClient webClient) {
        return webClient.mutate()
                .baseUrl(baseUserUri)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
