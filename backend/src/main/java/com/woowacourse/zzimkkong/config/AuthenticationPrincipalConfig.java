package com.woowacourse.zzimkkong.config;

import com.woowacourse.zzimkkong.infrastructure.auth.AuthenticationPrincipalArgumentResolver;
import com.woowacourse.zzimkkong.infrastructure.auth.LoginInterceptor;
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
                "/api/managers/token",
                "/api/managers/**",
                "/admin/api/**"
        );

        List<String> pathsToExclude = List.of(
                //manager join
                "/api/managers",
                "/api/managers/GOOGLE",
                "/api/managers/GITHUB",
                "/api/managers/google",
                "/api/managers/github",
                "/api/managers/oauth",

                //manager login
                "/api/managers/login/token",
                "/api/managers/GOOGLE/login/token",
                "/api/managers/GITHUB/login/token",
                "/api/managers/google/login/token",
                "/api/managers/github/login/token",

                //manager etc
                "/api/managers/emojis",

                //admin login
                "/admin/login/",
                "/admin/api/login"
        );

        registry.addInterceptor(loginInterceptor)
                .addPathPatterns(pathsToAdd)
                .excludePathPatterns(pathsToExclude);
    }
}
