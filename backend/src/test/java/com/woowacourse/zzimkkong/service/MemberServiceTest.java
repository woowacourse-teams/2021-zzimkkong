package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.member.MemberSaveRequest;
import com.woowacourse.zzimkkong.dto.member.MemberSaveResponse;
import com.woowacourse.zzimkkong.dto.member.MemberUpdateRequest;
import com.woowacourse.zzimkkong.dto.member.oauth.OauthMemberSaveRequest;
import com.woowacourse.zzimkkong.exception.member.DuplicateEmailException;
import com.woowacourse.zzimkkong.exception.member.ReservationExistsOnMemberException;
import com.woowacourse.zzimkkong.infrastructure.auth.LoginEmail;
import com.woowacourse.zzimkkong.infrastructure.oauth.OauthHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

class MemberServiceTest extends ServiceTest {
    @Autowired
    private MemberService memberService;

    @MockBean
    private OauthHandler oauthHandler;

    @Test
    @DisplayName("회원이 올바르게 저장을 요청하면 저장한다.")
    void saveMember() {
        //given
        MemberSaveRequest memberSaveRequest = new MemberSaveRequest(EMAIL, PW, ORGANIZATION);
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

        given(members.save(any(Member.class)))
                .willReturn(savedMember);

        //then
        MemberSaveResponse memberSaveResponse = MemberSaveResponse.from(savedMember);
        assertThat(memberService.saveMember(memberSaveRequest)).usingRecursiveComparison()
                .isEqualTo(memberSaveResponse);
    }

    @Test
    @DisplayName("회원이 중복된 이메일로 저장을 요청하면 오류가 발생한다.")
    void saveMemberException() {
        //given
        MemberSaveRequest memberSaveRequest = new MemberSaveRequest(EMAIL, PW, ORGANIZATION);

        //when
        given(members.existsByEmail(anyString()))
                .willReturn(true);

        //then
        assertThatThrownBy(() -> memberService.saveMember(memberSaveRequest))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"GOOGLE", "GITHUB"})
    @DisplayName("소셜 로그인을 이용해 회원가입한다.")
    void saveMemberByOauth(String oauth) {
        //given
        OauthMemberSaveRequest oauthMemberSaveRequest = new OauthMemberSaveRequest(EMAIL, ORGANIZATION, oauth);
        Member member = new Member(
                oauthMemberSaveRequest.getEmail(),
                oauthMemberSaveRequest.getOrganization(),
                oauthMemberSaveRequest.getOauthProvider()
        );
        given(members.existsByEmail(anyString()))
                .willReturn(false);

        //when
        Member savedMember = new Member(
                1L,
                member.getEmail(),
                member.getOrganization(),
                member.getOauthProvider());
        given(members.save(any(Member.class)))
                .willReturn(savedMember);

        //then
        MemberSaveResponse memberSaveResponse = MemberSaveResponse.from(savedMember);
        assertThat(memberService.saveMemberByOauth(oauthMemberSaveRequest)).usingRecursiveComparison()
                .isEqualTo(memberSaveResponse);
    }


    @ParameterizedTest
    @ValueSource(strings = {"GOOGLE", "GITHUB"})
    @DisplayName("이미 존재하는 이메일로 소셜 로그인을 이용해 회원가입하면 에러가 발생한다.")
    void saveMemberByOauthException(String oauth) {
        //given
        OauthMemberSaveRequest oauthMemberSaveRequest = new OauthMemberSaveRequest(EMAIL, ORGANIZATION, oauth);

        //when
        given(members.existsByEmail(anyString()))
                .willReturn(true);

        //then
        assertThatThrownBy(() -> memberService.saveMemberByOauth(oauthMemberSaveRequest))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    @DisplayName("회원은 자신의 정보를 수정할 수 있다.")
    void updateMember() {
        // given
        LoginEmail loginEmail = LoginEmail.from(EMAIL);
        Member member = new Member(EMAIL, PW, ORGANIZATION);
        MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest("woowabros");

        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        // when
        memberService.updateMember(loginEmail, memberUpdateRequest);

        assertThat(members.findByEmail(EMAIL).orElseThrow().getOrganization()).isEqualTo("woowabros");
    }

    @Test
    @DisplayName("회원을 삭제할 수 있다.")
    void deleteMember() {
        // given
        LoginEmail loginEmail = LoginEmail.from(EMAIL);
        Member pobi = new Member(EMAIL, PW, ORGANIZATION);
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(reservations.existsReservationsByMemberFromToday(any(Member.class)))
                .willReturn(false);

        // when, then
        assertDoesNotThrow(() -> memberService.deleteMember(loginEmail));
    }

    @Test
    @DisplayName("회원이 소유한 공간에 예약이 있다면 탈퇴할 수 없다.")
    void deleteMemberFailWhenAnyReservationsExists() {
        // given
        LoginEmail loginEmail = LoginEmail.from(EMAIL);
        Member pobi = new Member(EMAIL, PW, ORGANIZATION);
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(reservations.existsReservationsByMemberFromToday(any(Member.class)))
                .willReturn(true);

        // when, then
        assertThatThrownBy(() -> memberService.deleteMember(loginEmail))
                .isInstanceOf(ReservationExistsOnMemberException.class);
    }
}
