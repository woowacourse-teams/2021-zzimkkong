package com.woowacourse.zzimkkong.config.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class CustomDataSourceProperties {
    @Bean
    @ConfigurationProperties("app.datasource.master")
    public DataSourceProperties masterDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("app.datasource.master.hikari")
    public HikariDataSource masterDataSource() {
        return masterDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties("app.datasource.slave1")
    public DataSourceProperties slave1DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("app.datasource.slave1.hikari")
    public HikariDataSource slave1DataSource() {
        return slave1DataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }
}
