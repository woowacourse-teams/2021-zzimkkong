package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OauthProvider;
import com.woowacourse.zzimkkong.domain.oauth.GithubUserInfo;
import com.woowacourse.zzimkkong.domain.oauth.OauthUserInfo;
import com.woowacourse.zzimkkong.exception.infrastructure.oauth.UnableToGetTokenResponseFromGithubException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@PropertySource("classpath:config/oauth.properties")
public class GithubRequester implements OauthAPIRequester {
    private final String clientId;
    private final String secretId;
    private final WebClient githubOauthLoginClient;
    private final WebClient githubOpenApiClient;

    public GithubRequester(
            final String clientId,
            final String secretId,
            final String githubOauthUrl,
            final String githubOpenApiUrl) {
        this.clientId = clientId;
        this.secretId = secretId;
        this.githubOauthLoginClient = githubOauthLoginClient(githubOauthUrl);
        this.githubOpenApiClient = githubOpenApiClient(githubOpenApiUrl);
    }

    @Override
    public boolean supports(final OauthProvider oauthProvider) {
        return OauthProvider.GITHUB.equals(oauthProvider);
    }

    @Override
    public OauthUserInfo getUserInfoByCode(final String code) {
        String token = getToken(code);
        return getUserInfo(token);
    }

    private String getToken(final String code) {
        Map<String, Object> responseBody = githubOauthLoginClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/access_token")
                        .queryParam("code", code)
                        .queryParam("client_id", clientId)
                        .queryParam("client_secret", secretId)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .blockOptional()
                .orElseThrow(UnableToGetTokenResponseFromGithubException::new);

        return responseBody.get("access_token").toString();
    }

    private OauthUserInfo getUserInfo(final String token) {
        Map<String, Object> responseBody = githubOpenApiClient
                .get()
                .uri("/user")
                .header(HttpHeaders.AUTHORIZATION, "token " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .blockOptional()
                .orElseThrow(UnableToGetTokenResponseFromGithubException::new);

        return GithubUserInfo.from(responseBody);
    }

    private WebClient githubOauthLoginClient(String githubOauthUrl) {
        return WebClient.builder()
                .baseUrl(githubOauthUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private WebClient githubOpenApiClient(String githubOpenApiUrl) {
        return WebClient.builder()
                .baseUrl(githubOpenApiUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
