package com.woowacourse.zzimkkong.config.warmup;

import com.woowacourse.zzimkkong.infrastructure.thumbnail.BatikConverter;
import com.woowacourse.zzimkkong.infrastructure.warmup.Warmer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@PropertySource("classpath:config/s3proxy.properties")
@Profile("!test")
public class WarmerConfig {
    private final BatikConverter batikConverter;
    private final WebClient webClient;

    public WarmerConfig(
            final BatikConverter batikConverter,
            final WebClient webClient) {
        this.batikConverter = batikConverter;
        this.webClient = webClient;
    }

    @Bean(name = "warmer")
    @Profile("prod")
    public Warmer warmerProd(
            @Value("${s3proxy.server-uri.prod}") final String serverUri,
            @Value("${s3proxy.secret-key.prod}") final String secretKey) {
        return new Warmer(batikConverter, webClient, serverUri, secretKey);
    }

    @Bean(name = "warmer")
    @Profile({"dev", "local"})
    public Warmer warmerDev(
            @Value("${s3proxy.server-uri.dev}") final String serverUri,
            @Value("${s3proxy.secret-key.dev}") final String secretKey) {
        return new Warmer(batikConverter, webClient, serverUri, secretKey);
    }
}
