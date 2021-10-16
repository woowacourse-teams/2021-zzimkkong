package com.woowacourse.zzimkkong.config;

import com.woowacourse.zzimkkong.repository.*;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Configuration
public class LogProxyConfig {
    private static final String GROUP_NAME_OF_REPOSITORY = "repository";

    private final ConfigurableListableBeanFactory beanFactory;
    private final BeanDefinitionRegistry beanDefinitionRegistry;

    public LogProxyConfig(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        if (!(this.beanFactory instanceof BeanDefinitionRegistry)) {
            throw new IllegalArgumentException(); // todo 커스텀 예외
        }
        this.beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;
    }

    @Bean
    @PostConstruct
    void init() {
        final List<LogProxyClassEntry> beanClassesForReplacingByProxy = getBeanClassesForReplacingByProxy();
        for (LogProxyClassEntry logProxyClassEntry : beanClassesForReplacingByProxy) {
            replaceByProxy(logProxyClassEntry);
        }
    }

    protected List<LogProxyClassEntry> getBeanClassesForReplacingByProxy() {
        return List.of(
                new LogProxyClassEntry(MemberRepository.class, GROUP_NAME_OF_REPOSITORY),
                new LogProxyClassEntry(MapRepository.class, GROUP_NAME_OF_REPOSITORY),
                new LogProxyClassEntry(SpaceRepository.class, GROUP_NAME_OF_REPOSITORY),
                new LogProxyClassEntry(ReservationRepository.class, GROUP_NAME_OF_REPOSITORY),
                new LogProxyClassEntry(PresetRepository.class, GROUP_NAME_OF_REPOSITORY));
    }

    private void replaceByProxy(LogProxyClassEntry logProxyClassEntry) {
        final Class<?> proxyClass = logProxyClassEntry.getProxyClass();
        String beanName = getBeanName(proxyClass);

        final Object target = beanFactory.getBean(beanName);
        final BeanDefinition targetBeanDefinition = beanFactory.getBeanDefinition(beanName);

        final String transformedBeanName = BeanFactoryUtils.transformedBeanName(beanName);
        beanDefinitionRegistry.removeBeanDefinition(transformedBeanName);
        beanDefinitionRegistry.registerBeanDefinition(transformedBeanName, targetBeanDefinition);

        final Object logProxy = LogAspect.createLogProxy(target, proxyClass, logProxyClassEntry.getLogGroup());
        beanFactory.registerSingleton(transformedBeanName, logProxy);
    }

    private String getBeanName(Class<?> aClass) {
        String[] beanNames = beanFactory.getBeanNamesForType(aClass);
        if (beanNames.length == 0) {
            throw new IllegalArgumentException();   // todo 커스텀 예외
        }
        if (beanNames.length == 1) {
            return beanNames[0];
        }
        return getPrimaryBeanName(beanNames);
    }

    private String getPrimaryBeanName(String[] beanNames) {
        return Arrays.stream(beanNames)
                .filter(beanName -> beanFactory.getBeanDefinition(beanName).isPrimary())
                .findAny()
                .orElseThrow(); // todo 커스텀 예외
    }

    public static class LogProxyClassEntry {
        Class<?> clazz;
        String logGroup;

        public LogProxyClassEntry(Class<?> clazz, String logGroup) {
            this.clazz = clazz;
            this.logGroup = logGroup;
        }

        public Class<?> getProxyClass() {
            return clazz;
        }

        public String getLogGroup() {
            return logGroup;
        }
    }
}
