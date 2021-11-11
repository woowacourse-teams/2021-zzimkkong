package com.woowacourse.zzimkkong.infrastructure.transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class UUIDThreadLocalTest {
    private UUIDThreadLocal uuidThreadLocal;

    @BeforeEach
    void setUp() {
        uuidThreadLocal = new UUIDThreadLocal();
    }

    @Test
    @DisplayName("트랜잭션 아이디를 얻어올 수 있다.")
    void getTransactionId() {
        // when, then
        String transactionId = uuidThreadLocal.getTransactionId();

        assertThat(transactionId).isNotNull();
    }

    @Test
    @DisplayName("쓰레드 로컬의 현재 쓰레드에 할당된 값을 삭제한다.")
    void release() {
        assertDoesNotThrow(() -> uuidThreadLocal.release());
    }

    @Test
    @DisplayName("초기화할 수 있다.")
    void clearAll() {
        assertDoesNotThrow(() -> uuidThreadLocal.clearAll());
    }
}
