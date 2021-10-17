package com.woowacourse.zzimkkong.config.logaspect;

import com.woowacourse.zzimkkong.exception.config.logaspect.BeanFactoryInjectionFaultException;
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
    private final LogProxyBeanRegistry logProxyBeanRegistry = new LogProxyBeanRegistry();

    @Autowired
    public final void setBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        if (!(this.beanFactory instanceof BeanDefinitionRegistry)) {
            throw new BeanFactoryInjectionFaultException();
        }
        this.beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;
    }

    @PostConstruct
    protected final void init() {
        registerProxyBeans(logProxyBeanRegistry);

        final List<LogProxyClassEntry> beanClassesForReplacingByProxy = logProxyBeanRegistry.getLogProxyClassEntries();
        for (LogProxyClassEntry logProxyClassEntry : beanClassesForReplacingByProxy) {
            replaceByProxy(logProxyClassEntry.getProxyClass(), logProxyClassEntry.getLogGroup());
        }
    }

    abstract protected void registerProxyBeans(final LogProxyBeanRegistry logProxyBeanRegistry);

    private void replaceByProxy(Class<?> proxyClass, String logGroupName) {
        String[] beanNames = beanFactory.getBeanNamesForType(proxyClass);

        for (String beanName : beanNames) {
            replaceByProxyByBeamName(beanName, proxyClass, logGroupName);
        }
    }

    private void replaceByProxyByBeamName(String beanName, Class<?> proxyClass, String logGroupName) {
        final Object target = beanFactory.getBean(beanName);
        final BeanDefinition targetBeanDefinition = beanFactory.getBeanDefinition(beanName);

        final String transformedBeanName = BeanFactoryUtils.transformedBeanName(beanName);
        beanDefinitionRegistry.removeBeanDefinition(transformedBeanName);
        beanDefinitionRegistry.registerBeanDefinition(transformedBeanName, targetBeanDefinition);

        final Object logProxy = LogAspect.createLogProxy(target, proxyClass, logGroupName);
        beanFactory.registerSingleton(transformedBeanName, logProxy);
    }

    public final static class LogProxyBeanRegistry {
        private final List<LogProxyClassEntry> logProxyClassEntries = new ArrayList<>();

        private LogProxyBeanRegistry() {
        }

        public void add(Class<?> clazz, String logGroup) {
            this.logProxyClassEntries.add(new LogProxyClassEntry(clazz, logGroup));
        }

        public List<LogProxyClassEntry> getLogProxyClassEntries() {
            return logProxyClassEntries;
        }
    }

    private static class LogProxyClassEntry {
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
