package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.MemberSaveRequest;
import com.woowacourse.zzimkkong.dto.MemberSaveResponse;
import com.woowacourse.zzimkkong.exception.DuplicateEmailException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class MemberServiceTest extends ServiceTest {
    public static final String EMAIL = "pobi@email.com";
    public static final String PASSWORD = "test1234";
    public static final String ORGANIZATION = "루터";

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("회원이 올바르게 저장을 요청하면 저장한다.")
    void saveMember() {
        //given
        MemberSaveRequest memberSaveRequest = new MemberSaveRequest(EMAIL, PASSWORD, ORGANIZATION);
        Member member = new Member(
                memberSaveRequest.getEmail(),
                memberSaveRequest.getPassword(),
                memberSaveRequest.getOrganization()
        );

        //when
        Member savedMember = new Member(
                1L,
                member.getEmail(),
                member.getPassword(),
                member.getOrganization());

        given(memberRepository.save(any(Member.class)))
                .willReturn(savedMember);

        //then
        MemberSaveResponse memberSaveResponse = MemberSaveResponse.of(savedMember);
        assertThat(memberService.saveMember(memberSaveRequest)).usingRecursiveComparison()
                .isEqualTo(memberSaveResponse);
    }

    @Test
    @DisplayName("회원이 중복된 이메일로 저장을 요청하면 저장한다.")
    void saveMemberException() {
        //given
        MemberSaveRequest memberSaveRequest = new MemberSaveRequest(EMAIL, PASSWORD, ORGANIZATION);

        //when
        given(memberRepository.existsByEmail(any(String.class)))
                .willReturn(true);

        //then
        assertThatThrownBy(() -> memberService.saveMember(memberSaveRequest))
                .isInstanceOf(DuplicateEmailException.class);
    }
}
