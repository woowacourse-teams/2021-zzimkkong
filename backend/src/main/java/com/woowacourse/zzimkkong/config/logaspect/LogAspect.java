package com.woowacourse.zzimkkong.config.logaspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.reflections.Reflections;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import static net.logstash.logback.argument.StructuredArguments.value;

@Slf4j
@Component
@Aspect
public class LogAspect {
    private static final String GROUP_NAME_OF_REPOSITORY = "repository";

    private final List<Class<?>> repositoryClasses;

    public LogAspect() {
        Reflections reflections = new Reflections("com.woowacourse.zzimkkong");
        this.repositoryClasses = reflections.getTypesAnnotatedWith(LogMethodExecutionTime.class)
                .stream()
                .filter(Repository.class::isAssignableFrom)
                .collect(Collectors.toList());
    }

    @Around("@within(com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime)" +
            "&& !execution(public * org.springframework.data.repository.Repository+.*(..))" +
            "&& execution(public * *.*(..))")
    public Object logExecutionTimeOfClassWithAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        String logGroup = getLogGroupFromAnnotation(joinPoint);

        return proceedWithExecutionTime(joinPoint, methodSignature.getDeclaringType(), logGroup);
    }

    private String getLogGroupFromAnnotation(ProceedingJoinPoint joinPoint) {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        return targetClass.getAnnotation(LogMethodExecutionTime.class).group();
    }

    @Around("execution(public * org.springframework.data.repository.Repository+.*(..))")
    public Object logExecutionTimeOfRepository(ProceedingJoinPoint joinPoint) throws Throwable {
        Object target = joinPoint.getTarget();

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Class<?> declaringType = methodSignature.getDeclaringType();

        Class<?> typeToLog = repositoryClasses.stream()
                .filter(repositoryClass -> repositoryClass.isInstance(target))
                .findAny()
                .orElse(declaringType);

        return proceedWithExecutionTime(joinPoint, typeToLog, GROUP_NAME_OF_REPOSITORY);
    }

    private static Object proceedWithExecutionTime(ProceedingJoinPoint joinPoint, Class<?> typeToLog, String logGroup) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime;

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        logExecutionInfo(typeToLog, method, timeTaken, logGroup);

        return result;
    }

    private static void logExecutionInfo(Class<?> type, Method method, long timeTaken, String logGroup) {
        log.info("{} took {} ms. (info group by '{}')",
                value("method", type.getName() + "." + method.getName() + "()"),
                value("execution_time", timeTaken),
                value("group", logGroup));
    }
}
