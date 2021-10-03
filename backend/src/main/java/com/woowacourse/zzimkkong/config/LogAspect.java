package com.woowacourse.zzimkkong.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import static net.logstash.logback.argument.StructuredArguments.value;

@Slf4j
@Component
@Aspect
public class LogAspect {
    @Before("@target(com.woowacourse.zzimkkong.config.logaspect.LogEveryMethodCall) " +
            "&& execution(* com.woowacourse..*(..)))")
    public void logEveryMethodCall(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        log.info("API call: {}",
                value("method", methodSignature.getDeclaringTypeName() + "." + methodSignature.getName() + "()"));
    }
}
