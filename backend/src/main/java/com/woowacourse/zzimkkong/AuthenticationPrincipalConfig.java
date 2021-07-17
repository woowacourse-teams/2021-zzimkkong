package com.woowacourse.zzimkkong;

import com.woowacourse.zzimkkong.infrastructure.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class AuthenticationPrincipalConfig implements WebMvcConfigurer {
    private final LoginInterceptor loginInterceptor;

    public AuthenticationPrincipalConfig(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> interceptorPaths = List.of(
                "/api/members/token",
                "/api/providers/maps/*/reservations/*"
        );

        registry.addInterceptor(loginInterceptor)
                .addPathPatterns(interceptorPaths)
                .excludePathPatterns();
    }
}
