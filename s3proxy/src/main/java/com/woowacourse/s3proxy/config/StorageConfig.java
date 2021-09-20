package com.woowacourse.s3proxy.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class StorageConfig {

    @Bean
    @Profile({"prod", "dev"})
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder
                .standard()
                .build();
    }

    @Bean
    @Profile({"test", "local"})
    public AmazonS3 amazonS3Local(@Value("cloud.aws.region.static") String region) {
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .build();
    }
}
