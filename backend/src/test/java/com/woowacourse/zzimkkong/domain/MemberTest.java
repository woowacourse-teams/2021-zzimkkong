package com.woowacourse.zzimkkong.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.woowacourse.zzimkkong.CommonFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {
    @Test
    @DisplayName("비밀번호가 일치하면 true를 반환한다.")
    void checkPassword() {
        Member member = new Member(EMAIL, PASSWORD, ORGANIZATION);

        assertThat(member.checkPassword(PASSWORD)).isTrue();
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 false를 반환한다.")
    void checkWrongPassword() {
        Member member = new Member(EMAIL, PASSWORD, ORGANIZATION);

        assertThat(member.checkPassword(PASSWORD + "%")).isFalse();
    }
}
