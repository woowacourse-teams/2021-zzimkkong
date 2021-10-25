package com.woowacourse.s3proxy.config;

import com.woowacourse.s3proxy.infrastructure.AuthInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config/s3proxy.properties")
public class AuthInterceptorConfig {
    @Bean(name = "authInterceptor")
    @Profile("prod")
    public AuthInterceptor authInterceptorProd(
            @Value("${s3proxy.secret-key.prod}") String secretKey) {
        return new AuthInterceptor(secretKey);
    }

    @Bean(name = "authInterceptor")
    @Profile({"dev", "local", "test"})
    public AuthInterceptor authInterceptorDev(
            @Value("${s3proxy.secret-key.dev}") String secretKey) {
        return new AuthInterceptor(secretKey);
    }
}
