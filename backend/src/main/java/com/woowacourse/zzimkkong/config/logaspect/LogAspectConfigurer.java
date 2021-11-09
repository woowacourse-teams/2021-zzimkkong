package com.woowacourse.zzimkkong.config.logaspect;

import com.woowacourse.zzimkkong.exception.config.logaspect.InvalidModifiableBeanFactoryException;
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
    private final LogEntries logEntries = new LogEntries();

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
        registerBeans(logEntries);

        final List<LogEntry> logTargetEntries = logEntries.getLogTargetEntries();

        for (LogEntry logEntry : logTargetEntries) {
            replaceByProxy(logEntry.getTargetClass(), logEntry.getLogGroup());
        }
    }

    abstract protected void registerBeans(final LogEntries logEntries);

    private void replaceByProxy(Class<?> targetClass, String logGroupName) {
        String[] beanNames = beanFactory.getBeanNamesForType(targetClass);

        for (String beanName : beanNames) {
            replaceByProxy(beanName, targetClass, logGroupName);
        }
    }

    private void replaceByProxy(String beanName, Class<?> targetClass, String logGroupName) {
        final Object target = beanFactory.getBean(beanName);
        final BeanDefinition targetBeanDefinition = beanFactory.getBeanDefinition(beanName);

        beanDefinitionRegistry.removeBeanDefinition(beanName);
        beanDefinitionRegistry.registerBeanDefinition(beanName, targetBeanDefinition);

        final Object logProxy = LogAspect.createLogProxy(target, targetClass, logGroupName);
        beanFactory.registerSingleton(beanName, logProxy);
    }

    public final static class LogEntries {
        private final List<LogEntry> entries = new ArrayList<>();

        private LogEntries() {
        }

        public void add(Class<?> clazz, String logGroup) {
            this.entries.add(new LogEntry(clazz, logGroup));
        }

        private List<LogEntry> getLogTargetEntries() {
            return entries;
        }
    }

    private static class LogEntry {
        Class<?> targetClass;
        String logGroup;

        private LogEntry(Class<?> targetClass, String logGroup) {
            this.targetClass = targetClass;
            this.logGroup = logGroup;
        }

        private Class<?> getTargetClass() {
            return targetClass;
        }

        private String getLogGroup() {
            return logGroup;
        }
    }
}
