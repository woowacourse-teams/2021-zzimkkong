package com.woowacourse.zzimkkong.config.logaspect;

import com.woowacourse.zzimkkong.infrastructure.transaction.TransactionThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static net.logstash.logback.argument.StructuredArguments.value;

@Slf4j
@Component
@Aspect
@Order(2)
public class LogAspect {
    private final TransactionThreadLocal transactionThreadLocal;

    public LogAspect(final TransactionThreadLocal transactionThreadLocal) {
        this.transactionThreadLocal = transactionThreadLocal;
    }

    @Around("@target(com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime)" +
            "&& allZzimkkongPublicMethod()")
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

    void logExecutionInfo(MethodSignature methodSignature, long timeTaken, String logGroup) {
        final Class<?> declaringType = methodSignature.getDeclaringType();
        final Method method = methodSignature.getMethod();

        logExecutionInfo(declaringType, method, timeTaken, logGroup);
    }

    void logExecutionInfo(Class<?> typeToLog, Method method, long timeTaken, String logGroup) {
        String transactionId = transactionThreadLocal.getTransactionId();

        log.info("{} took {} ms. (info group: '{}', transactionId: {})",
                value("method", typeToLog.getName() + "." + method.getName() + "()"),
                value("execution_time", timeTaken),
                value("group", logGroup),
                value("transaction", transactionId));
    }

    Object createLogProxy(Object target, Class<?> typeToLog, String logGroup) {
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
        advisor.setExpression("execution(public * com.woowacourse.zzimkkong..*(..))");

        ExecutionTimeLogAdvice advice = new ExecutionTimeLogAdvice(this, typeToLog, logGroup);
        advisor.setAdvice(advice);

        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvisor(advisor);
        proxyFactory.setProxyTargetClass(true);

        return proxyFactory.getProxy();
    }

    private String getLogGroupFromAnnotation(ProceedingJoinPoint joinPoint) {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        return targetClass.getAnnotation(LogMethodExecutionTime.class).group();
    }

    @Pointcut("execution(public * com.woowacourse.zzimkkong..*(..))")
    private void allZzimkkongPublicMethod() {}
}
