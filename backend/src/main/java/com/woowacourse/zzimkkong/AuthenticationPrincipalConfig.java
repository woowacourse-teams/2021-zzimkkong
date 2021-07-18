package com.woowacourse.zzimkkong;

import com.woowacourse.zzimkkong.infrastructure.AuthenticationPrincipalArgumentResolver;
import com.woowacourse.zzimkkong.infrastructure.JwtUtils;
import com.woowacourse.zzimkkong.infrastructure.LoginInterceptor;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class AuthenticationPrincipalConfig implements WebMvcConfigurer {
    private final LoginInterceptor loginInterceptor;
    private final JwtUtils jwtUtils;
    private final MemberRepository members;

    public AuthenticationPrincipalConfig(final LoginInterceptor loginInterceptor, final JwtUtils jwtUtils, final MemberRepository members) {
        this.loginInterceptor = loginInterceptor;
        this.jwtUtils = jwtUtils;
        this.members = members;
    }

    @Override
    public void addArgumentResolvers(List argumentResolvers) {
        argumentResolvers.add(createAuthenticationPrincipalArgumentResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/members/token");
    }

    @Bean
    public AuthenticationPrincipalArgumentResolver createAuthenticationPrincipalArgumentResolver() {
        return new AuthenticationPrincipalArgumentResolver(jwtUtils, members);
    }
}
