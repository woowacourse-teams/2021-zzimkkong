package com.woowacourse.zzimkkong.config;

import com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import static net.logstash.logback.argument.StructuredArguments.value;

@Slf4j
@Component
@Aspect
public class LogAspect {

    @Around("@target(com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime) " +
            "&& execution(* com.woowacourse..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime;

        String logGroup = getLogGroup(joinPoint);

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        log.info("{} took {} ms. (info group by '{}')",
                value("method", methodSignature.getDeclaringTypeName() + "." + methodSignature.getName() + "()"),
                value("execution_time", timeTaken),
                value("group", logGroup));

        return result;
    }

    private String getLogGroup(ProceedingJoinPoint joinPoint) {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        String logGroup = targetClass.getAnnotation(LogMethodExecutionTime.class).group();
        return logGroup;
    }

    @Around("execution(public * org.springframework.data.repository.Repository+.*(..))")
    public Object logExecutionTimeOfRepository(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime;

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String logGroup = "repository";
        log.info("{} took {} ms. (info group by '{}')",
                value("method", methodSignature.getDeclaringTypeName() + "." + methodSignature.getName() + "()"),
                value("execution_time", timeTaken),
                value("group", logGroup));

        return result;
    }
}
