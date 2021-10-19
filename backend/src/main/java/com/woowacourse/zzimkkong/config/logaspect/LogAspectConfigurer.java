package com.woowacourse.zzimkkong.config.logaspect;

import com.woowacourse.zzimkkong.exception.config.logaspect.InvalidModifiableBeanFactoryException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

public abstract class LogAspectConfigurer {
    private ConfigurableListableBeanFactory beanFactory;
    private BeanDefinitionRegistry beanDefinitionRegistry;
    private final LogProxyRegistrationEntries logProxyRegistrationEntries = new LogProxyRegistrationEntries();

    @Autowired
    public final void setBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        if (!(beanFactory instanceof BeanDefinitionRegistry)) {
            throw new InvalidModifiableBeanFactoryException();
        }
        this.beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;
    }

    @PostConstruct
    protected final void init() {
        registerProxyBeans(logProxyRegistrationEntries);

        final List<LogProxyRegistrationEntry> beanClassesForReplacingByProxy = logProxyRegistrationEntries.getLogProxyRegistrationEntry();
        for (LogProxyRegistrationEntry logProxyRegistrationEntry : beanClassesForReplacingByProxy) {
            replaceByProxy(logProxyRegistrationEntry.getBeanClass(), logProxyRegistrationEntry.getLogGroup());
        }
    }

    abstract protected void registerProxyBeans(final LogProxyRegistrationEntries logProxyRegistrationEntries);

    private void replaceByProxy(Class<?> beanClass, String logGroupName) {
        String[] beanNames = beanFactory.getBeanNamesForType(beanClass);

        for (String beanName : beanNames) {
            replaceByProxy(beanName, beanClass, logGroupName);
        }
    }

    private void replaceByProxy(String beanName, Class<?> proxyClass, String logGroupName) {
        final Object target = beanFactory.getBean(beanName);
        final BeanDefinition targetBeanDefinition = beanFactory.getBeanDefinition(beanName);

        final String transformedBeanName = BeanFactoryUtils.transformedBeanName(beanName);
        beanDefinitionRegistry.removeBeanDefinition(transformedBeanName);
        beanDefinitionRegistry.registerBeanDefinition(transformedBeanName, targetBeanDefinition);

        final Object logProxy = LogAspect.createLogProxy(target, proxyClass, logGroupName);
        beanFactory.registerSingleton(transformedBeanName, logProxy);
    }

    public final static class LogProxyRegistrationEntries {
        private final List<LogProxyRegistrationEntry> logProxyRegistrationEntry = new ArrayList<>();

        private LogProxyRegistrationEntries() {
        }

        public void add(Class<?> clazz, String logGroup) {
            this.logProxyRegistrationEntry.add(new LogProxyRegistrationEntry(clazz, logGroup));
        }

        public List<LogProxyRegistrationEntry> getLogProxyRegistrationEntry() {
            return logProxyRegistrationEntry;
        }
    }

    private static class LogProxyRegistrationEntry {
        Class<?> beanClass;
        String logGroup;

        public LogProxyRegistrationEntry(Class<?> beanClass, String logGroup) {
            this.beanClass = beanClass;
            this.logGroup = logGroup;
        }

        public Class<?> getBeanClass() {
            return beanClass;
        }

        public String getLogGroup() {
            return logGroup;
        }
    }
}
