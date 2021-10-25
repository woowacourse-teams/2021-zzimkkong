package com.woowacourse.zzimkkong.config;

import com.woowacourse.zzimkkong.infrastructure.thumbnail.S3ProxyUploader;
import com.woowacourse.zzimkkong.infrastructure.thumbnail.StorageUploader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config/s3proxy.properties")
public class S3ProxyConfig {
    @Bean(name = "storageUploader")
    @Profile("prod")
    public StorageUploader storageUploaderProd(
            @Value("${s3proxy.server-uri.prod}") final String serverUri,
            @Value("${s3proxy.secret-key.prod}") final String secretKey) {
        return new S3ProxyUploader(serverUri, secretKey);
    }

    @Bean(name = "storageUploader")
    @Profile({"dev", "local", "test"})
    public StorageUploader storageUploaderDev(
            @Value("${s3proxy.server-uri.dev}") final String serverUri,
            @Value("${s3proxy.secret-key.dev}") final String secretKey) {
        return new S3ProxyUploader(serverUri, secretKey);
    }
}
