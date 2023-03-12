package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.OauthProvider;
import com.woowacourse.zzimkkong.domain.ProfileEmoji;
import com.woowacourse.zzimkkong.dto.member.MemberSaveRequest;
import com.woowacourse.zzimkkong.dto.member.MemberSaveResponse;
import com.woowacourse.zzimkkong.dto.member.MemberUpdateRequest;
import com.woowacourse.zzimkkong.dto.member.oauth.OauthMemberSaveRequest;
import com.woowacourse.zzimkkong.exception.member.DuplicateEmailException;
import com.woowacourse.zzimkkong.exception.member.DuplicateUserNameException;
import com.woowacourse.zzimkkong.exception.member.ReservationExistsOnMemberException;
import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
import com.woowacourse.zzimkkong.infrastructure.oauth.OauthHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
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
        MemberSaveRequest memberSaveRequest = new MemberSaveRequest(EMAIL, POBI, ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST, PW);
        Member member = Member.builder()
                .email(memberSaveRequest.getEmail())
                .userName(memberSaveRequest.getUserName())
                .emoji(ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST)
                .password(memberSaveRequest.getPassword())
                .build();

        //when
        Member savedMember = Member.builder()
                .id(1L)
                .email(member.getEmail())
                .userName(member.getUserName())
                .emoji(ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST)
                .password(member.getPassword())
                .organization(member.getOrganization())
                .build();

        given(members.save(any(Member.class)))
                .willReturn(savedMember);

        //then
        MemberSaveResponse memberSaveResponse = MemberSaveResponse.from(savedMember);
        assertThat(memberService.saveMember(memberSaveRequest)).usingRecursiveComparison()
                .isEqualTo(memberSaveResponse);
    }

    @Test
    @DisplayName("회원이 중복된 이메일로 저장을 요청하면 오류가 발생한다.")
    void saveMemberEmailException() {
        //given
        MemberSaveRequest memberSaveRequest = new MemberSaveRequest(EMAIL, POBI, ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST, PW);

        //when
        given(members.existsByEmail(anyString()))
                .willReturn(true);

        //then
        assertThatThrownBy(() -> memberService.saveMember(memberSaveRequest))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    @DisplayName("회원이 중복된 유저네임으로 저장을 요청하면 오류가 발생한다.")
    void saveMemberUserNameException() {
        //given
        MemberSaveRequest memberSaveRequest = new MemberSaveRequest(EMAIL, POBI, ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST, PW);

        //when
        given(members.existsByEmail(anyString()))
                .willReturn(false);
        given(members.existsByUserName(anyString()))
                .willReturn(true);

        //then
        assertThatThrownBy(() -> memberService.saveMember(memberSaveRequest))
                .isInstanceOf(DuplicateUserNameException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"GOOGLE", "GITHUB"})
    @DisplayName("소셜 로그인을 이용해 회원가입한다.")
    void saveMemberByOauth(String oauth) {
        //given
        OauthMemberSaveRequest oauthMemberSaveRequest = new OauthMemberSaveRequest(EMAIL, POBI, ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST, oauth);
        Member member = Member.builder()
                .email(oauthMemberSaveRequest.getEmail())
                .userName(oauthMemberSaveRequest.getUserName())
                .emoji(ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST)
                .oauthProvider(OauthProvider.valueOfWithIgnoreCase(oauthMemberSaveRequest.getOauthProvider()))
                .build();

        given(members.existsByEmail(anyString()))
                .willReturn(false);

        //when
        Member savedMember = Member.builder()
                .id(1L)
                .email(member.getEmail())
                .userName(member.getUserName())
                .emoji(ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST)
                .organization(member.getOrganization())
                .oauthProvider(member.getOauthProvider())
                .build();
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
    void saveMemberByOauthEmailException(String oauth) {
        //given
        OauthMemberSaveRequest oauthMemberSaveRequest = new OauthMemberSaveRequest(EMAIL, POBI, ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST, oauth);

        //when
        given(members.existsByEmail(anyString()))
                .willReturn(true);

        //then
        assertThatThrownBy(() -> memberService.saveMemberByOauth(oauthMemberSaveRequest))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"GOOGLE", "GITHUB"})
    @DisplayName("이미 존재하는 유저 네임으로 소셜 로그인을 이용해 회원가입하면 에러가 발생한다.")
    void saveMemberByOauthUserNameException(String oauth) {
        //given
        OauthMemberSaveRequest oauthMemberSaveRequest = new OauthMemberSaveRequest(EMAIL, POBI, ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST, oauth);

        //when
        given(members.existsByEmail(anyString()))
                .willReturn(false);
        given(members.existsByUserName(anyString()))
                .willReturn(true);

        //then
        assertThatThrownBy(() -> memberService.saveMemberByOauth(oauthMemberSaveRequest))
                .isInstanceOf(DuplicateUserNameException.class);
    }

    @Test
    @DisplayName("회원은 자신의 정보를 수정할 수 있다.")
    void updateMember() {
        // given
        LoginUserEmail loginUserEmail = LoginUserEmail.from(EMAIL);
        Member member = Member.builder()
                .email(EMAIL)
                .userName(POBI)
                .emoji(ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST)
                .password(PW)
                .organization(ORGANIZATION)
                .build();
        MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest("sakjung", ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST);

        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        // when
        memberService.updateMember(loginUserEmail, memberUpdateRequest);

        assertThat(members.findByEmail(EMAIL).orElseThrow().getUserName()).isEqualTo("sakjung");
    }

    @Test
    @DisplayName("회원이 자신의 정보를 수정할 때 중복된 닉네임을 입력하면 실패한다.")
    void updateMemberFailWhenDuplicatedUsername() {
        // given
        LoginUserEmail loginUserEmail = LoginUserEmail.from(EMAIL);
        Member member = Member.builder()
                .email(EMAIL)
                .userName(POBI)
                .emoji(ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST)
                .password(PW)
                .organization(ORGANIZATION)
                .build();
        MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest("sakjung", ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST);

        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(member));
        given(members.existsByUserName(anyString()))
                .willReturn(true);

        // when, then
        assertThatThrownBy(() -> memberService.updateMember(loginUserEmail, memberUpdateRequest))
                .isInstanceOf(DuplicateUserNameException.class);
    }

    @Test
    @DisplayName("회원이 자신의 정보를 수정할 때, 자신의 닉네임은 중복 검사에서 제외한다.")
    void updateMemberIgnoreUsernameValidationWhenGivenUsernameAsIs() {
        // given
        LoginUserEmail loginUserEmail = LoginUserEmail.from(EMAIL);
        Member member = Member.builder()
                .email(EMAIL)
                .userName(POBI)
                .emoji(ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST)
                .password(PW)
                .organization(ORGANIZATION)
                .build();
        MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest(member.getUserName(), ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST);

        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(member));
        given(members.existsByUserName(anyString()))
                .willReturn(true);

        // when, then
        assertDoesNotThrow(() -> memberService.updateMember(loginUserEmail, memberUpdateRequest));
    }

    @Test
    @DisplayName("회원을 삭제할 수 있다.")
    void deleteMember() {
        // given
        LoginUserEmail loginUserEmail = LoginUserEmail.from(EMAIL);
        Member pobi = Member.builder()
                .email(EMAIL)
                .userName(POBI)
                .emoji(ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST)
                .password(PW)
                .organization(ORGANIZATION)
                .build();
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(reservations.existsByMemberAndEndTimeAfter(any(Member.class), any(LocalDateTime.class)))
                .willReturn(false);

        // when, then
        assertDoesNotThrow(() -> memberService.deleteMember(loginUserEmail));
    }

    @Test
    @DisplayName("회원이 소유한 공간에 예약이 있다면 탈퇴할 수 없다.")
    void deleteMemberFailWhenAnyReservationsExists() {
        // given
        LoginUserEmail loginUserEmail = LoginUserEmail.from(EMAIL);
        Member pobi = Member.builder()
                .email(EMAIL)
                .userName(POBI)
                .emoji(ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST)
                .password(PW)
                .organization(ORGANIZATION)
                .build();
        given(members.findByEmail(anyString()))
                .willReturn(Optional.of(pobi));
        given(reservations.existsByMemberAndEndTimeAfter(any(Member.class), any(LocalDateTime.class)))
                .willReturn(true);

        // when, then
        assertThatThrownBy(() -> memberService.deleteMember(loginUserEmail))
                .isInstanceOf(ReservationExistsOnMemberException.class);
    }

    @Test
    @DisplayName("중복된 유저 네임이 들어오면 에러를 반환한다")
    void validateDuplicateUserName() {
        //given

        //when
        given(members.existsByEmail(anyString()))
                .willReturn(false);
        given(members.existsByUserName(anyString()))
                .willReturn(true);

        //then
        assertThatThrownBy(() -> memberService.validateDuplicateUserName("중복된이름"))
                .isInstanceOf(DuplicateUserNameException.class);
    }
}
