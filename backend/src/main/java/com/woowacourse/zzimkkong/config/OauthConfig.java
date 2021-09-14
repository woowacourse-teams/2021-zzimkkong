package com.woowacourse.zzimkkong.config;

import com.woowacourse.zzimkkong.infrastructure.oauth.StringToOauthProviderConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class OauthConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToOauthProviderConverter());
    }
}
