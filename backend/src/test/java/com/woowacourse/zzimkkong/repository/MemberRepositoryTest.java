package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.exception.NoSuchMemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import static com.woowacourse.zzimkkong.CommonFixture.EMAIL;
import static com.woowacourse.zzimkkong.CommonFixture.POBI;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MemberRepositoryTest extends RepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("Member를 저장한다.")
    void save() {
        // given, when
        Member expected = memberRepository.save(POBI);

        // then
        assertThat(expected.getId()).isNotNull();
        assertThat(expected).usingRecursiveComparison()
                .isEqualTo(POBI);
    }

    @Test
    @DisplayName("저장된 멤버를 이메일을 통해 찾아올 수 있다.")
    void findByEmail() {
        // given
        Member expected = memberRepository.save(POBI);

        // when
        Member findMember = memberRepository.findByEmail(EMAIL)
                .orElseThrow(NoSuchMemberException::new);

        // then
        assertThat(findMember).isEqualTo(expected);
    }

    @Test
    @DisplayName("특정 이메일을 가진 멤버가 있는지 확인할 수 있다.")
    void existsByEmail() {
        // given
        memberRepository.save(POBI);

        // when
        boolean actual = memberRepository.existsByEmail(EMAIL);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("중복된 이메일을 가진 멤버를 등록하면 에러가 발생한다.")
    void duplicatedEmail() {
        // given
        memberRepository.save(POBI);

        // when
        Member sameEmailMember = new Member(EMAIL, "another123", "루터회관");

        // then
        assertThatThrownBy(() -> memberRepository.save(sameEmailMember))
                .isInstanceOf(DataAccessException.class);
    }
}
