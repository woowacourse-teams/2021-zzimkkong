package com.woowacourse.zzimkkong.config.datasource;

import com.woowacourse.zzimkkong.exception.infrastructure.NoMasterDataSourceException;
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
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@Profile("prod")
public class CustomDataSourceConfig {
    public static final String MASTER = "master";
    public static final String SLAVE = "slave";
    private static final String PACKAGE_PATH = "com.woowacourse.zzimkkong";
    private final List<HikariDataSource> hikariDataSources;
    private final JpaProperties jpaProperties;

    public CustomDataSourceConfig(final List<HikariDataSource> hikariDataSources, final JpaProperties jpaProperties) {
        this.hikariDataSources = hikariDataSources;
        this.jpaProperties = jpaProperties;
    }

    @Bean
    public DataSource dataSource() {
        return new LazyConnectionDataSourceProxy(routingDataSource());
    }

    @Bean
    public DataSource routingDataSource() {
        final DataSource master = createMasterDataSource();
        final Map<Object, Object> slaves = createSlaveDataSources();
        slaves.put(MASTER, master);

        ReplicationRoutingDataSource replicationRoutingDataSource = new ReplicationRoutingDataSource();
        replicationRoutingDataSource.setDefaultTargetDataSource(master);
        replicationRoutingDataSource.setTargetDataSources(slaves);
        return replicationRoutingDataSource;
    }

    private DataSource createMasterDataSource() {
        return hikariDataSources.stream()
                .filter(dataSource -> dataSource.getPoolName().startsWith(MASTER))
                .findFirst()
                .orElseThrow(NoMasterDataSourceException::new);
    }

    private Map<Object, Object> createSlaveDataSources() {
        final List<HikariDataSource> slaveDataSources = hikariDataSources.stream()
                .filter(datasource -> Objects.nonNull(datasource.getPoolName()) && datasource.getPoolName().startsWith(SLAVE))
                .collect(Collectors.toList());

        final Map<Object, Object> result = new HashMap<>();
        for (final HikariDataSource slaveDataSource : slaveDataSources) {
            result.put(slaveDataSource.getPoolName(), slaveDataSource);
        }
        return result;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        EntityManagerFactoryBuilder entityManagerFactoryBuilder = createEntityManagerFactoryBuilder(jpaProperties);
        return entityManagerFactoryBuilder.dataSource(dataSource()).packages(PACKAGE_PATH).build();
    }

    private EntityManagerFactoryBuilder createEntityManagerFactoryBuilder(JpaProperties jpaProperties) {
        AbstractJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        return new EntityManagerFactoryBuilder(vendorAdapter, jpaProperties.getProperties(), null);
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(entityManagerFactory);
        return tm;
    }
}
