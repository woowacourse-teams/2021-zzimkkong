package com.woowacourse.zzimkkong.config.logaspect;

import com.woowacourse.zzimkkong.infrastructure.transaction.TransactionThreadLocal;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Order(1)
@Component
public class TransactionReleaser {
    private final TransactionThreadLocal transactionThreadLocal;

    public TransactionReleaser(TransactionThreadLocal transactionThreadLocal) {
        this.transactionThreadLocal = transactionThreadLocal;
    }

    @Pointcut("execution(public * com.woowacourse.zzimkkong..*(..))")
    private void allZzimkkongPublicMethod() {
    }

    @Pointcut("@target(org.springframework.web.bind.annotation.RestController)")
    private void allRestController() {
    }

    @Pointcut("@target(org.springframework.stereotype.Controller)")
    private void allController() {
    }

    @After("(allRestController() || allController() ) && allZzimkkongPublicMethod()")
    public void releaseTransactionId() {
        transactionThreadLocal.release();
    }
}
