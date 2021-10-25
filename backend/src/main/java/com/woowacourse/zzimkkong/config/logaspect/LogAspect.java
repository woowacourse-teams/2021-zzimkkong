package com.woowacourse.zzimkkong.config.logaspect;

import com.woowacourse.zzimkkong.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.value;

@Slf4j
@Component
@Aspect
public class LogAspect {
    private static final String GROUP_NAME_OF_REPOSITORY = "repository";

    private final List<Class<?>> repositoryClasses = List.of(
            MemberRepository.class,
            PresetRepository.class,
            MapRepository.class,
            SpaceRepository.class,
            ReservationRepository.class
    );

    @Around("@within(com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime)" +
            "&& execution(public * *.*(..))")
    public Object logExecutionTimeOfClassWithAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime;

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final Class<?> declaringType = methodSignature.getDeclaringType();
        final Method method = methodSignature.getMethod();

        String logGroup = getLogGroupFromAnnotation(joinPoint);

        logExecutionInfo(declaringType, method, timeTaken, logGroup);

        return result;
    }

    @Around("execution(public * org.springframework.data.repository.Repository+.*(..))")
    public Object logExecutionTimeOfRepository(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime;

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Class<?> declaringType = methodSignature.getDeclaringType();

        Object target = joinPoint.getTarget();
        Class<?> typeToLog = repositoryClasses.stream()
                .filter(repositoryClass -> repositoryClass.isInstance(target))
                .findAny()
                .orElse(declaringType);

        logExecutionInfo(typeToLog, method, timeTaken, GROUP_NAME_OF_REPOSITORY);

        return result;
    }

    private String getLogGroupFromAnnotation(ProceedingJoinPoint joinPoint) {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        return targetClass.getAnnotation(LogMethodExecutionTime.class).group();
    }

    private static void logExecutionInfo(Class<?> type, Method method, long timeTaken, String logGroup) {
        log.info("{} took {} ms. (info group by '{}')",
                value("method", type.getName() + "." + method.getName() + "()"),
                value("execution_time", timeTaken),
                value("group", logGroup));
    }
}
