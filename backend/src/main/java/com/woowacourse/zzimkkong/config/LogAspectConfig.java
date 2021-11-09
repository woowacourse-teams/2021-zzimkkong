package com.woowacourse.zzimkkong.config;

import com.woowacourse.zzimkkong.config.logaspect.LogAspectConfigurer;
import com.woowacourse.zzimkkong.config.logaspect.LogRegistry;
import org.reflections.Reflections;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class LogAspectConfig extends LogAspectConfigurer {
    @Override
    protected void registerBeans(LogEntries logEntries) {
        Reflections reflections = new Reflections("com.woowacourse.zzimkkong");
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(LogRegistry.class);

        for (Class<?> clazz : typesAnnotatedWith) {
            String logGroup = clazz.getAnnotation(LogRegistry.class).group();
            logEntries.add(clazz, logGroup);
        }
    }
}
