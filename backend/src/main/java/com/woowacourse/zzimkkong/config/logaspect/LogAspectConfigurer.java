package com.woowacourse.zzimkkong.config.logaspect;

import com.woowacourse.zzimkkong.exception.config.logaspect.BeanFactoryInjectionFaultException;
import com.woowacourse.zzimkkong.exception.config.logaspect.BeanToReplaceDoesNotExistsException;
import com.woowacourse.zzimkkong.exception.config.logaspect.NoPriorityOnBeanToReplaceException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
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
            replaceByProxy(logProxyClassEntry);
        }
    }

    abstract protected void registerProxyBeans(LogProxyBeanRegistry logProxyBeanRegistry);

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

    private String getBeanName(Class<?> clazz) {
        String[] beanNames = beanFactory.getBeanNamesForType(clazz);
        if (beanNames.length == 0) {
            throw new BeanToReplaceDoesNotExistsException(clazz);
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
                .orElseThrow(); // todo 다수의 빈을 찾으면 모두 프록시로 변경
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
