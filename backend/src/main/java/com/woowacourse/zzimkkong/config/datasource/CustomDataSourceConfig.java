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

    private final List<HikariDataSource> hikariDataSources;
    private final JpaProperties jpaProperties;

    public CustomDataSourceConfig(final List<HikariDataSource> hikariDataSources, final JpaProperties jpaProperties) {
        this.hikariDataSources = hikariDataSources;
        this.jpaProperties = jpaProperties;
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
        final DataSource master = createMasterDataSource();
        final Map<Object, Object> slaves = createSlaveDataSources();
        slaves.put("master", master);

        ReplicationRoutingDataSource replicationRoutingDataSource = new ReplicationRoutingDataSource();
        replicationRoutingDataSource.setDefaultTargetDataSource(master);
        replicationRoutingDataSource.setTargetDataSources(slaves);
        return replicationRoutingDataSource;
    }

    private DataSource createMasterDataSource() {
        return hikariDataSources.stream()
                .filter(dataSource -> dataSource.getPoolName().startsWith("master"))
                .findFirst()
                .orElseThrow(NoMasterDataSourceException::new);
    }

    private Map<Object, Object> createSlaveDataSources() {
        final List<HikariDataSource> slaveDataSources = hikariDataSources.stream()
                .filter(datasource -> Objects.nonNull(datasource.getPoolName()) && datasource.getPoolName().startsWith("slave"))
                .collect(Collectors.toList());

        final Map<Object, Object> result = new HashMap<>();
        for (final HikariDataSource slaveDataSource : slaveDataSources) {
            result.put(slaveDataSource.getPoolName(), slaveDataSource);
        }
        return result;
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
