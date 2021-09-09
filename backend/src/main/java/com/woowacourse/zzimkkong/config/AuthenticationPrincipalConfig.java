package com.woowacourse.zzimkkong.config;

import com.woowacourse.zzimkkong.infrastructure.AuthenticationPrincipalArgumentResolver;
import com.woowacourse.zzimkkong.infrastructure.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class AuthenticationPrincipalConfig implements WebMvcConfigurer {
    private final LoginInterceptor loginInterceptor;
    private final AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver;

    public AuthenticationPrincipalConfig(final LoginInterceptor loginInterceptor, final AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver) {
        this.loginInterceptor = loginInterceptor;
        this.authenticationPrincipalArgumentResolver = authenticationPrincipalArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List argumentResolvers) {
        argumentResolvers.add(authenticationPrincipalArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> pathsToAdd = List.of(
                "/api/members/token",
                "/api/managers/**"
        );

        registry.addInterceptor(loginInterceptor)
                .addPathPatterns(pathsToAdd);
    }
}
