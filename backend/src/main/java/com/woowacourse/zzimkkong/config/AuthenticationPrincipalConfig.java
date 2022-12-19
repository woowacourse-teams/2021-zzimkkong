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
                "/api/members/token",
                "/api/members/**",
                "/api/managers/**",
                "/admin/api/**"
        );

        List<String> pathsToExclude = List.of(
                //manager join
                "/api/members",
                "/api/members/GOOGLE",
                "/api/members/GITHUB",
                "/api/members/google",
                "/api/members/github",
                "/api/members/oauth",

                //manager login
                "/api/members/login/token",
                "/api/members/GOOGLE/login/token",
                "/api/members/GITHUB/login/token",
                "/api/members/google/login/token",
                "/api/members/github/login/token",

                //manager etc
                "/api/members/emojis",

                //admin login
                "/admin/login/",
                "/admin/api/login"
        );

        registry.addInterceptor(loginInterceptor)
                .addPathPatterns(pathsToAdd)
                .excludePathPatterns(pathsToExclude);
    }
}
