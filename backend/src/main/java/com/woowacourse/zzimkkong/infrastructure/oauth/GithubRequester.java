package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OAuthProvider;
import com.woowacourse.zzimkkong.domain.oauth.GithubUserInfo;
import com.woowacourse.zzimkkong.domain.oauth.OAuthUserInfo;
import com.woowacourse.zzimkkong.exception.infrastructure.oauth.UnableToGetTokenResponseFromGithubException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@PropertySource("classpath:config/oauth.properties")
public class GithubRequester implements OAuthAPIRequester {

    private final String clientId;
    private final String secretId;
    private final WebClient githubOAuthLoginClient;
    private final WebClient githubOpenApiClient;

    public GithubRequester(
            @Value("${github.client-id}") final String clientId,
            @Value("${github.secret-id}") final String secretId,
            @Value("${github.url.oauth-login}") final String githubOAuthUrl,
            @Value("${github.url.open-api}") final String githubOpenApiUrl) {
        this.clientId = clientId;
        this.secretId = secretId;
        this.githubOAuthLoginClient = githubOAuthLoginClient(githubOAuthUrl);
        this.githubOpenApiClient = githubOpenApiClient(githubOpenApiUrl);
    }

    @Override
    public boolean supports(final OAuthProvider oAuthProvider) {
        return OAuthProvider.GITHUB.equals(oAuthProvider);
    }

    @Override
    public OAuthUserInfo getUserInfoByCode(final String code) {
        String token = getToken(code);
        return getUserInfo(token);
    }

    private String getToken(final String code) {
        Map<String, Object> responseBody = githubOAuthLoginClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/access_token")
                        .queryParam("code", code)
                        .queryParam("client_id", clientId)
                        .queryParam("client_secret", secretId)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .blockOptional()
                .orElseThrow(UnableToGetTokenResponseFromGithubException::new);

        return responseBody.get("access_token").toString();
    }

    private OAuthUserInfo getUserInfo(final String token) {
        Map<String, Object> responseBody = githubOpenApiClient
                .get()
                .uri("/user")
                .header(HttpHeaders.AUTHORIZATION, "token " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .blockOptional()
                .orElseThrow(UnableToGetTokenResponseFromGithubException::new);

        return GithubUserInfo.from(responseBody);
    }

    private WebClient githubOAuthLoginClient(String githubOAuthUrl) {
        return WebClient.builder()
                .baseUrl(githubOAuthUrl)
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
