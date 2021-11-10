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

    protected LogProxyPostProcessor() {
        Reflections reflections = new Reflections("com.woowacourse.zzimkkong");
        typesAnnotatedWith = Collections.unmodifiableSet(reflections.getTypesAnnotatedWith(FindInstanceAndCreateLogProxy.class));
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return typesAnnotatedWith.stream()
                .filter(typeToLog -> typeToLog.isAssignableFrom(bean.getClass()))
                .findAny()
                .map(typeToLog -> createProxy(bean, typeToLog))
                .orElse(bean);
    }

    private Object createProxy(Object bean, Class<?> typeToLog) {
        FindInstanceAndCreateLogProxy annotation = typeToLog.getAnnotation(FindInstanceAndCreateLogProxy.class);
        String groupName = annotation.group();
        return LogAspect.createLogProxy(bean, typeToLog, groupName);
    }
}
