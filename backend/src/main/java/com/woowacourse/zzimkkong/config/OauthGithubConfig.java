package com.woowacourse.zzimkkong.config;

import com.woowacourse.zzimkkong.infrastructure.oauth.GithubRequester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@PropertySource("classpath:config/oauth.properties")
public class OauthGithubConfig {
    @Autowired
    private WebClient webClient;

    @Bean(name = "githubRequester")
    @Profile({"prod"})
    public GithubRequester githubRequesterProd(
            @Value("${github.client-id.prod}") final String clientId,
            @Value("${github.secret-id.prod}") final String secretId,
            @Value("${github.url.oauth-login}") final String githubOauthUrl,
            @Value("${github.url.open-api}") final String githubOpenApiUrl) {
        return new GithubRequester(clientId, secretId, githubOauthUrl, githubOpenApiUrl, webClient);
    }

    @Bean(name = "githubRequester")
    @Profile({"dev"})
    public GithubRequester githubRequesterDev(
            @Value("${github.client-id.dev}") final String clientId,
            @Value("${github.secret-id.dev}") final String secretId,
            @Value("${github.url.oauth-login}") final String githubOauthUrl,
            @Value("${github.url.open-api}") final String githubOpenApiUrl) {
        return new GithubRequester(clientId, secretId, githubOauthUrl, githubOpenApiUrl, webClient);
    }

    @Bean(name = "githubRequester")
    @Profile({"local", "test"})
    public GithubRequester githubRequesterLocalTest(
            @Value("${github.client-id.local}") final String clientId,
            @Value("${github.secret-id.local}") final String secretId,
            @Value("${github.url.oauth-login}") final String githubOauthUrl,
            @Value("${github.url.open-api}") final String githubOpenApiUrl) {
        return new GithubRequester(clientId, secretId, githubOauthUrl, githubOpenApiUrl, webClient);
    }
}
