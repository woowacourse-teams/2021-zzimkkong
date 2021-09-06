package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OAuthProvider;
import com.woowacourse.zzimkkong.domain.oauth.GithubUserInfo;
import com.woowacourse.zzimkkong.domain.oauth.OAuthUserInfo;
import com.woowacourse.zzimkkong.exception.infrastructure.oauth.UnableToGetTokenResponseFromGithubException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

public class GithubRequester implements OAuthAPIRequester {

    private final String clientId;
    private final String secretId;
    private final WebClient githubOAuthLoginClient;
    private final WebClient githubOpenApiClient;

    public GithubRequester(final String clientId,
                           final String secretId,
                           final WebClient githubOAuthLoginClient,
                           final WebClient githubOpenApiClient) {
        this.clientId = clientId;
        this.secretId = secretId;
        this.githubOAuthLoginClient = githubOAuthLoginClient;
        this.githubOpenApiClient = githubOpenApiClient;
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


}
