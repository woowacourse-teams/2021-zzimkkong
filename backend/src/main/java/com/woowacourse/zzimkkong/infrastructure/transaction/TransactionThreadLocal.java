package com.woowacourse.zzimkkong.infrastructure.transaction;

public interface TransactionThreadLocal {
    String getTransactionId();

    void release();

    void clearAll();
}
