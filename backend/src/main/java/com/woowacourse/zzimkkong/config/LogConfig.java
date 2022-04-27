package com.woowacourse.zzimkkong.config;

import com.woowacourse.zzimkkong.config.logaspect.LogTraceIdInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LogConfig implements WebMvcConfigurer {
    private final LogTraceIdInterceptor logTraceIdInterceptor;

    public LogConfig(final LogTraceIdInterceptor logTraceIdInterceptor) {
        this.logTraceIdInterceptor = logTraceIdInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logTraceIdInterceptor)
                .addPathPatterns("/**");
    }
}
