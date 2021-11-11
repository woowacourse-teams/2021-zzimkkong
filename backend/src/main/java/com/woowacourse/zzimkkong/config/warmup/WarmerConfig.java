package com.woowacourse.zzimkkong.config.warmup;

import com.woowacourse.zzimkkong.domain.SlackUrl;
import com.woowacourse.zzimkkong.infrastructure.thumbnail.BatikConverter;
import com.woowacourse.zzimkkong.infrastructure.transaction.TransactionThreadLocal;
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
    private final SlackUrl slackUrl;
    private final WebClient webClient;
    private final TransactionThreadLocal transactionThreadLocal;

    public WarmerConfig(
            final BatikConverter batikConverter,
            final SlackUrl slackUrl,
            final WebClient webClient,
            final TransactionThreadLocal transactionThreadLocal) {
        this.batikConverter = batikConverter;
        this.slackUrl = slackUrl;
        this.webClient = webClient;
        this.transactionThreadLocal = transactionThreadLocal;
    }

    @Bean(name = "warmer")
    @Profile("prod")
    public Warmer warmerProd(
            @Value("${s3proxy.server-uri.prod}") final String serverUri,
            @Value("${s3proxy.secret-key.prod}") final String secretKey) {
        return new Warmer(batikConverter, slackUrl, webClient, serverUri, secretKey, transactionThreadLocal);
    }

    @Bean(name = "warmer")
    @Profile({"dev", "local"})
    public Warmer warmerDev(
            @Value("${s3proxy.server-uri.dev}") final String serverUri,
            @Value("${s3proxy.secret-key.dev}") final String secretKey) {
        return new Warmer(batikConverter, slackUrl, webClient, serverUri, secretKey, transactionThreadLocal);
    }
}
