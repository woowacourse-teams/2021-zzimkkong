package com.woowacourse.zzimkkong.config.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;

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

    /**
     * datasource 생성
     */
    public DataSource createDataSource(String url) {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url(url)
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .username(databaseProperty.getUsername())
                .password(databaseProperty.getPassword())
                .build();
    }

    /**
     * 실제 쿼리가 실행될 때 Connection을 가져오기
     */
    @Bean
    public DataSource dataSource() {
        return new LazyConnectionDataSourceProxy(routingDataSource());
    }

    /**
     * CustomDataSourceProperties를 통해 master, slave Datasource 생성 후
     * ReplicationRoutingDataSource에 등록
     */
    @Bean
    public DataSource routingDataSource() {
        DataSource master = createDataSource(databaseProperty.getUrl());

        Map<Object, Object> dataSourceMap = new LinkedHashMap<>();
        dataSourceMap.put("master", master);
        databaseProperty.getSlave()
                .forEach((key, value) -> dataSourceMap.put(value.getName(), createDataSource(value.getUrl())));

        ReplicationRoutingDataSource replicationRoutingDataSource = new ReplicationRoutingDataSource();
        replicationRoutingDataSource.setDefaultTargetDataSource(master);
        replicationRoutingDataSource.setTargetDataSources(dataSourceMap);
        return replicationRoutingDataSource;
    }

    /**
     * JPA에서 사용할 EntityManagerFactory 설정
     * hibernate 설정 직접 주입
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        EntityManagerFactoryBuilder entityManagerFactoryBuilder = createEntityManagerFactoryBuilder(jpaProperties);
        return entityManagerFactoryBuilder.dataSource(dataSource()).packages("com.woowacourse.zzimkkong").build();
    }

    private EntityManagerFactoryBuilder createEntityManagerFactoryBuilder(JpaProperties jpaProperties) {
        AbstractJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        return new EntityManagerFactoryBuilder(vendorAdapter, jpaProperties.getProperties(), null);
    }

    /**
     * JPA에서 사용할 TransactionManager 설정
     */
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(entityManagerFactory);
        return tm;
    }
}
