package com.woowacourse.zzimkkong.config.logaspect;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.reflections.Reflections;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;

import static net.logstash.logback.argument.StructuredArguments.value;

@Slf4j
@Component
@Aspect
public class LogAspect {

    @Around("@target(com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime)" +
            "&& execution(public * com.woowacourse.zzimkkong..*(..))")
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

    static Object createLogProxy(Object target, Class<?> typeToLog, String logGroup) {
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
        advisor.setExpression("execution(public * com.woowacourse.zzimkkong..*(..))");

        ExecutionTimeLogAdvice advice = new ExecutionTimeLogAdvice(typeToLog, logGroup);
        advisor.setAdvice(advice);

        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvisor(advisor);
        proxyFactory.setProxyTargetClass(true);

        return proxyFactory.getProxy();
    }

    private static class ExecutionTimeLogAdvice implements MethodInterceptor {
        private final Class<?> typeToLog;
        private final String logGroup;

        private ExecutionTimeLogAdvice(Class<?> typeToLog, String logGroup) {
            this.typeToLog = typeToLog;
            this.logGroup = logGroup;
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            long startTime = System.currentTimeMillis();
            final Object result = invocation.proceed();
            long endTime = System.currentTimeMillis();
            long timeTaken = endTime - startTime;

            Method method = invocation.getMethod();
            logExecutionInfo(typeToLog, method, timeTaken, logGroup);

            return result;
        }
    }

    @Component
    private static class LogProxyPostProcessor implements BeanPostProcessor {
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
                    .map(typeToLog -> createLogProxy(bean, typeToLog))
                    .orElse(bean);
        }

        private Object createLogProxy(Object bean, Class<?> typeToLog) {
            FindInstanceAndCreateLogProxy annotation = typeToLog.getAnnotation(FindInstanceAndCreateLogProxy.class);
            String groupName = annotation.group();
            return LogAspect.createLogProxy(bean, typeToLog, groupName);
        }
    }
}
