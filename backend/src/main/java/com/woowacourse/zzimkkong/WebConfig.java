package com.woowacourse.zzimkkong;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpHead;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("*")
                .exposedHeaders(HttpHeaders.LOCATION);
//                .allowedOriginPatterns("https://zzimkkong-proxy.o-r.kr/");
    }
}
