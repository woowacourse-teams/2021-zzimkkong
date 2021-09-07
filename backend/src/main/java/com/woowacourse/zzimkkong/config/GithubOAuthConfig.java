package com.woowacourse.zzimkkong.config;

import com.woowacourse.zzimkkong.infrastructure.oauth.GithubRequester;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@PropertySource("classpath:config/oauth.properties")
public class GithubOAuthConfig {
    public static final String BASE_URL_GITHUB_OAUTH_LOGIN = "https://github.com/login/oauth";
    public static final String BASE_URL_GITHUB_OPEN_API = "https://api.github.com";

    @Bean
    public GithubRequester githubRequester(
            @Value("${github.client-id}") final String clientId,
            @Value("${github.secret-id}") final String secretId) {

        return new GithubRequester(clientId, secretId, githubOAuthLoginClient(), githubOpenApiClient());
    }

    public WebClient githubOAuthLoginClient() {
        return WebClient.builder()
                .baseUrl(BASE_URL_GITHUB_OAUTH_LOGIN)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public WebClient githubOpenApiClient() {
        return WebClient.builder()
                .baseUrl(BASE_URL_GITHUB_OPEN_API)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
