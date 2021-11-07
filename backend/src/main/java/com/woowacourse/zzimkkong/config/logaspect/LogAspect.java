package com.woowacourse.zzimkkong.config.logaspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static net.logstash.logback.argument.StructuredArguments.value;

@Slf4j
@Component
@Aspect
public class LogAspect {
    @Around("@within(com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime)" +
            "&& execution(public * *.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime;

        String logGroup = getLogGroupFromAnnotation(joinPoint);

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        logExecutionInfo(methodSignature, timeTaken, logGroup);

        return result;
    }

    private String getLogGroupFromAnnotation(ProceedingJoinPoint joinPoint) {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        return targetClass.getAnnotation(LogMethodExecutionTime.class).group();
    }

    private static void logExecutionInfo(MethodSignature methodSignature, long timeTaken, String logGroup) {
        final Class<?> declaringType = methodSignature.getDeclaringType();
        final Method method = methodSignature.getMethod();

        logExecutionInfo(declaringType, method, timeTaken, logGroup);
    }

    private static void logExecutionInfo(Class<?> typeToLog, Method method, long timeTaken, String logGroup) {
        log.info("{} took {} ms. (info group by '{}')",
                value("method", typeToLog.getName() + "." + method.getName() + "()"),
                value("execution_time", timeTaken),
                value("group", logGroup));
    }

    static <T> T createLogProxy(Object target, Class<T> typeToLog, String logGroup) {
        final LogProxyHandler logProxyHandler = new LogProxyHandler(target, typeToLog, logGroup);
        return typeToLog.cast(
                Proxy.newProxyInstance(
                        typeToLog.getClassLoader(),
                        new Class[]{typeToLog},
                        logProxyHandler));
    }

    private static class LogProxyHandler implements InvocationHandler {
        private final Object target;
        private final Class<?> typeToLog;
        private final String logGroup;

        private LogProxyHandler(Object target, Class<?> typeToLog, String logGroup) {
            this.target = target;
            this.typeToLog = typeToLog;
            this.logGroup = logGroup;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            long startTime = System.currentTimeMillis();
            final Object invokeResult = method.invoke(target, args);
            long endTime = System.currentTimeMillis();
            long timeTaken = endTime - startTime;

            logExecutionInfo(typeToLog, method, timeTaken, logGroup);

            return invokeResult;
        }
    }
}
