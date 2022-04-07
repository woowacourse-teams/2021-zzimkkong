package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime;
import com.woowacourse.zzimkkong.domain.OauthProvider;
import com.woowacourse.zzimkkong.domain.oauth.GithubUserInfo;
import com.woowacourse.zzimkkong.domain.oauth.OauthUserInfo;
import com.woowacourse.zzimkkong.exception.infrastructure.oauth.ErrorResponseToGetAccessTokenException;
import com.woowacourse.zzimkkong.exception.infrastructure.oauth.UnableToGetTokenResponseFromGithubException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@PropertySource("classpath:config/oauth.properties")
@LogMethodExecutionTime(group = "infrastructure")
public class GithubRequester implements OauthAPIRequester {
    private final String clientId;
    private final String secretId;
    private final WebClient githubOauthLoginClient;
    private final WebClient githubOpenApiClient;

    public GithubRequester(
            final String clientId,
            final String secretId,
            final String githubOauthUrl,
            final String githubOpenApiUrl,
            final WebClient webClient) {
        this.clientId = clientId;
        this.secretId = secretId;
        this.githubOauthLoginClient = githubOauthLoginClient(webClient, githubOauthUrl);
        this.githubOpenApiClient = githubOpenApiClient(webClient, githubOpenApiUrl);
    }

    @Override
    public boolean supports(final OauthProvider oauthProvider) {
        return oauthProvider.isSameAs(OauthProvider.GITHUB);
    }

    @Override
    public OauthUserInfo getUserInfoByCode(final String code) {
        String token = getToken(code);
        return getUserInfo(token);
    }

    private String getToken(final String code) {
        log.error("github client id: {}", clientId);
        Map<String, Object> responseBody = githubOauthLoginClient
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
        validateResponseBody(responseBody);
        return responseBody.get("access_token").toString();
    }

    private void validateResponseBody(Map<String, Object> responseBody) {
        if (!responseBody.containsKey("access_token")) {
            throw new ErrorResponseToGetAccessTokenException(responseBody.get("error_description").toString());
        }
    }

    private OauthUserInfo getUserInfo(final String token) {
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

    private WebClient githubOauthLoginClient(final WebClient webClient, final String githubOauthUrl) {
        return webClient.mutate()
                .baseUrl(githubOauthUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private WebClient githubOpenApiClient(final WebClient webClient, String githubOpenApiUrl) {
        return webClient.mutate()
                .baseUrl(githubOpenApiUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
