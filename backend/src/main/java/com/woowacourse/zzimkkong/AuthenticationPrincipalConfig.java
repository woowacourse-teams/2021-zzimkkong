package com.woowacourse.zzimkkong;

import com.woowacourse.zzimkkong.infrastructure.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@Configuration
public class AuthenticationPrincipalConfig extends WebConfig {
    private final LoginInterceptor loginInterceptor;

    public AuthenticationPrincipalConfig(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/members/token");
    }
}
