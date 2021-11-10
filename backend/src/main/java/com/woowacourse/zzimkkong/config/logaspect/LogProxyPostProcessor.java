package com.woowacourse.zzimkkong.config.logaspect;

import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
public class LogProxyPostProcessor implements BeanPostProcessor {
    private final Set<Class<?>> typesAnnotatedWith;

    public LogProxyPostProcessor() {
        Reflections reflections = new Reflections("com.woowacourse.zzimkkong");
        typesAnnotatedWith = Collections.unmodifiableSet(reflections.getTypesAnnotatedWith(LogRegistry.class));
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        for (Class<?> typeToLog : typesAnnotatedWith) {
            if (typeToLog.isAssignableFrom(bean.getClass())) {
                return createProxy(bean, typeToLog);
            }
        }
        return bean;
    }

    private Object createProxy(Object bean, Class<?> typeToLog) {
        LogRegistry annotation = typeToLog.getAnnotation(LogRegistry.class);
        String groupName = annotation.group();
        return LogAspect.createLogProxy(bean, typeToLog, groupName);
    }
}
