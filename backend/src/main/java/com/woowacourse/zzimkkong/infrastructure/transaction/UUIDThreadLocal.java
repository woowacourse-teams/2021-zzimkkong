package com.woowacourse.zzimkkong.infrastructure.transaction;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDThreadLocal implements TransactionThreadLocal {
    private ThreadLocal<UUID> threadLocal = new ThreadLocal<>();

    @Override
    public String getTransactionId() {
        UUID transactionId = threadLocal.get();
        if (transactionId == null) {
            transactionId = UUID.randomUUID();
            threadLocal.set(transactionId);
        }
        return useLast12Bytes(transactionId.toString());
    }

    @Override
    public void release() {
        threadLocal.remove();
    }

    @Override
    public void clearAll() {
        threadLocal = new ThreadLocal<>();
    }

    private String useLast12Bytes(String origin) {
        return origin.substring(24);
    }
}

