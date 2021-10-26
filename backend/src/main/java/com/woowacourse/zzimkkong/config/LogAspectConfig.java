package com.woowacourse.zzimkkong.config;

import com.woowacourse.zzimkkong.config.logaspect.LogAspectConfigurer;
import org.reflections.Reflections;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class LogAspectConfig extends LogAspectConfigurer {
    @Override
    protected void registerBeans(LogRegistry logRegistry) {
        Reflections reflections = new Reflections("com.woowacourse.zzimkkong");
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(com.woowacourse.zzimkkong.config.logaspect.LogRegistry.class);

        for (Class<?> clazz : typesAnnotatedWith) {
            String logGroup = clazz.getAnnotation(com.woowacourse.zzimkkong.config.logaspect.LogRegistry.class).group();
            logRegistry.add(clazz, logGroup);
        }
    }
}
