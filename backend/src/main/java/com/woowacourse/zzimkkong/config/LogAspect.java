package com.woowacourse.zzimkkong.config;

import com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static net.logstash.logback.argument.StructuredArguments.value;

@Slf4j
@Configuration
@Aspect
public class LogAspect {
    private static final String GROUP_NAME_OF_REPOSITORY = "repository";

    @Around("@target(com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime) " +
            "&& execution(* com.woowacourse..*(..))")
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

    private static void logExecutionInfo(Class<?> declaringType, Method method, long timeTaken, String logGroup) {
        log.info("{} took {} ms. (info group by '{}')",
                value("method", declaringType.getName() + "." + method.getName() + "()"),
                value("execution_time", timeTaken),
                value("group", logGroup));
    }

    @Bean(name = "memberRepositoryLogProxy")
    @Primary
    public MemberRepository memberRepository(MemberRepository memberRepository) {
        return createLogProxy(memberRepository, MemberRepository.class, GROUP_NAME_OF_REPOSITORY);
    }

    @Bean(name = "mapRepositoryLogProxy")
    @Primary
    public MapRepository mapRepository(MapRepository mapRepository) {
        return createLogProxy(mapRepository, MapRepository.class, GROUP_NAME_OF_REPOSITORY);
    }

    @Bean(name = "spaceRepositoryLogProxy")
    @Primary
    public SpaceRepository spaceRepository(SpaceRepository spaceRepository) {
        return createLogProxy(spaceRepository, SpaceRepository.class, GROUP_NAME_OF_REPOSITORY);
    }

    @Bean(name = "reservationRepositoryLogProxy")
    @Primary
    public ReservationRepository reservationRepository(ReservationRepository reservationRepository) {
        return createLogProxy(reservationRepository, ReservationRepository.class, GROUP_NAME_OF_REPOSITORY);
    }

    private <T> T createLogProxy(Object target, Class<T> requiredType, String logGroup) {
        final LogProxyHandler logProxyHandler = new LogProxyHandler(target, requiredType, logGroup);
        return requiredType.cast(Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{requiredType},
                logProxyHandler));
    }

    public static class LogProxyHandler implements InvocationHandler {
        private final Object target;
        private final Class<?> declaringType;
        private final String logGroup;

        public LogProxyHandler(Object target, Class<?> declaringType, String logGroup) {
            this.target = target;
            this.declaringType = declaringType;
            this.logGroup = logGroup;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            long startTime = System.currentTimeMillis();
            final Object invokeResult = method.invoke(target, args);
            long endTime = System.currentTimeMillis();
            long timeTaken = endTime - startTime;

            logExecutionInfo(declaringType, method, timeTaken, logGroup);

            return invokeResult;
        }
    }
}
