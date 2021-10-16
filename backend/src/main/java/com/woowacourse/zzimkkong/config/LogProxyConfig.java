package com.woowacourse.zzimkkong.config;

import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;

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
                new LogProxyClassEntry(ReservationRepository.class, GROUP_NAME_OF_REPOSITORY));
    }

    private void replaceByProxy(LogProxyClassEntry logProxyClassEntry) {
        final Class<?> proxyClass = logProxyClassEntry.getProxyClass();
        String beanName = getBeanName(proxyClass);
        final Object target = beanFactory.getBean(beanName);

        final String transformedBeanName = BeanFactoryUtils.transformedBeanName(beanName);

        final BeanDefinition targetBeanDefinition = beanFactory.getBeanDefinition(transformedBeanName);
        RootBeanDefinition proxyBeanDefinition = copyBeanDefinitionFrom(targetBeanDefinition);

        beanDefinitionRegistry.removeBeanDefinition(transformedBeanName);
        beanDefinitionRegistry.registerBeanDefinition(transformedBeanName, proxyBeanDefinition);

        final Object logProxy = LogAspect.createLogProxy(target, proxyClass, logProxyClassEntry.getLogGroup());
        beanFactory.registerSingleton(transformedBeanName, logProxy);
    }

    private RootBeanDefinition copyBeanDefinitionFrom(BeanDefinition from) {
        final ResolvableType resolvableType = from.getResolvableType();
        final RootBeanDefinition to = new RootBeanDefinition(resolvableType.resolve());
        to.setTargetType(resolvableType);
        to.setPrimary(from.isPrimary());
        copyQualifierIfExists(from, to);
        return to;
    }

    private void copyQualifierIfExists(BeanDefinition from, RootBeanDefinition to) {
        if (from instanceof AbstractBeanDefinition) {
            ((AbstractBeanDefinition) from).getQualifiers()
                    .forEach(to::addQualifier);
        }
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

    public MemberRepository memberRepository(MemberRepository memberRepository) {
        return LogAspect.createLogProxy(memberRepository, MemberRepository.class, GROUP_NAME_OF_REPOSITORY);
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
