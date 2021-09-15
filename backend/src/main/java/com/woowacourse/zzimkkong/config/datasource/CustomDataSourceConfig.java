package com.woowacourse.zzimkkong.config.datasource;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class}) // DataSource 자동 설정을 제외시킨다.
@EnableConfigurationProperties(CustomDataSourceProperties.class)
public class CustomDataSourceConfig {

    private final CustomDataSourceProperties databaseProperty;
    private final JpaProperties jpaProperties;

    public CustomDataSourceConfig(CustomDataSourceProperties databaseProperty, JpaProperties jpaProperties) {
        this.databaseProperty = databaseProperty;
        this.jpaProperties = jpaProperties;
    }
}
