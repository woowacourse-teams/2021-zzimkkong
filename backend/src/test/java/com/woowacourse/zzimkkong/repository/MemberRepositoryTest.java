package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.exception.NoSuchEmailException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MemberRepositoryTest extends RepositoryTest {
    public static final String EMAIL = "pobi@email.com";
    public static final String PASSWORD = "test1234";
    public static final String ORGANIZATION = "루터";

    @Autowired
    private MemberRepository memberRepository;

    public static final Member MEMBER = new Member(EMAIL, PASSWORD, ORGANIZATION);

    @Test
    @DisplayName("저장된 멤버를 이메일을 통해 찾아올 수 있다.")
    void findByEmail() {
        // given
        Member expected = memberRepository.save(MEMBER);

        // when
        Member findMember = memberRepository.findByEmail(EMAIL)
                .orElseThrow(NoSuchEmailException::new);

        // then
        assertThat(findMember).isEqualTo(expected);
    }

    @Test
    @DisplayName("특정 이메일을 가진 멤버가 있는지 확인할 수 있다.")
    void existsByEmail() {
        // given
        memberRepository.save(MEMBER);

        // when
        boolean actual = memberRepository.existsByEmail(EMAIL);

        // then
        assertThat(actual).isTrue();
    }
}
