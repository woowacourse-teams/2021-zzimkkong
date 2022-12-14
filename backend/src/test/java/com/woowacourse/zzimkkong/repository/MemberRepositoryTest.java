package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.exception.member.NoSuchMemberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MemberRepositoryTest extends RepositoryTest {
    private Member pobi;

    @BeforeEach
    void setUp() {
        pobi = Member.builder()
                .email(EMAIL)
                .userName(USER_NAME)
                .password(PW)
                .organization(ORGANIZATION)
                .build();
    }

    @Test
    @DisplayName("Member를 저장한다.")
    void save() {
        // given, when
        Member savedMember = members.save(pobi);

        // then
        assertThat(savedMember.getId()).isNotNull();
        assertThat(savedMember).isEqualTo(pobi);
    }

    @Test
    @DisplayName("저장된 멤버를 이메일을 통해 찾아올 수 있다.")
    void findByEmail() {
        // given
        Member savedMember = members.save(pobi);

        // when
        Member findMember = members.findByEmail(EMAIL)
                .orElseThrow(NoSuchMemberException::new);

        // then
        assertThat(findMember).isEqualTo(savedMember);
    }

    @Test
    @DisplayName("특정 이메일을 가진 멤버가 있는지 확인할 수 있다.")
    void existsByEmail() {
        // given
        members.save(pobi);

        // when, then
        assertThat(members.existsByEmail(EMAIL)).isTrue();
    }

    @Test
    @DisplayName("중복된 이메일을 가진 멤버를 등록하면 에러가 발생한다.")
    void duplicatedEmail() {
        // given
        members.save(pobi);

        // when
        Member sameEmailMember = Member.builder()
                .email(EMAIL)
                .userName("삭정")
                .password("another123")
                .organization("루터회관")
                .build();

        // then
        assertThatThrownBy(() -> members.save(sameEmailMember))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    @DisplayName("page로 모든 회원을 조회한다.")
    void findAll() {
        // given
        Member save = members.save(pobi);

        // when
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.unsorted());
        Page<Member> actual = members.findAll(pageRequest);

        // then
        assertThat(actual.getSize()).isEqualTo(20);
        assertThat(actual.getContent().size()).isEqualTo(1);
        assertThat(actual.getContent().get(0)).usingRecursiveComparison()
                .isEqualTo(save);
    }
}
