package com.woowacourse.zzimkkong.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {
    public static final String EMAIL = "pobi@email.com";
    public static final String PASSWORD = "test1234";
    public static final String ORGANIZATION = "루터";

    @Test
    @DisplayName("비밀번호가 일치하면 true를 반환한다.")
    void checkPassword() {
        Member member = new Member(EMAIL, PASSWORD, ORGANIZATION);

        assertThat(member.checkPassword("test1234")).isTrue();
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 반환한다.")
    void checkWrongPassword() {
        Member member = new Member(EMAIL, PASSWORD, ORGANIZATION);

        assertThat(member.checkPassword("test123")).isFalse();
    }
}
