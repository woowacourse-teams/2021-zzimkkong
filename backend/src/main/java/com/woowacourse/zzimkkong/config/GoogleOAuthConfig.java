package com.woowacourse.zzimkkong.config;

import com.woowacourse.zzimkkong.infrastructure.oauth.GoogleRequester;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config/oauth.properties")
public class GoogleOAuthConfig {
    private static final String BASE_URL_GOOGLE_OAUTH_LOGIN = "https://www.googleapis.com/oauth2/v4/token";
    private static final String BASE_URL_GOOGLE_USER_INFO = "https://www.googleapis.com/oauth2/v2/userinfo";
    private static final String REDIRECT_URL = "http://localhost:8080";

    @Bean
    public GoogleRequester googleRequester(
            @Value("${google.client-id}") final String clientId,
            @Value("${google.secret-id}") final String secretId) {

        return new GoogleRequester(clientId, secretId, REDIRECT_URL, BASE_URL_GOOGLE_OAUTH_LOGIN, BASE_URL_GOOGLE_USER_INFO);
    }
}
