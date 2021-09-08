package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.OAuthProvider;
import com.woowacourse.zzimkkong.domain.oauth.GoogleUserInfo;
import com.woowacourse.zzimkkong.dto.member.MemberSaveRequest;
import com.woowacourse.zzimkkong.dto.member.MemberSaveResponse;
import com.woowacourse.zzimkkong.dto.member.OAuthMemberSaveRequest;
import com.woowacourse.zzimkkong.dto.member.OAuthReadyResponse;
import com.woowacourse.zzimkkong.exception.member.DuplicateEmailException;
import com.woowacourse.zzimkkong.infrastructure.oauth.GoogleRequester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

class MemberServiceTest extends ServiceTest {
    @Autowired
    private MemberService memberService;

    @MockBean
    private GoogleRequester googleRequester;

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

    @Test
    @DisplayName("Google OAuth를 통해 얻을 수 없는 정보를 응답하며 회원가입 과정을 진행한다.")
    void getUserInfoFromOAuth() {
        //given
        given(googleRequester.supports(any(OAuthProvider.class)))
                .willReturn(true);
        given(googleRequester.getUserInfoByCode(anyString()))
                .willReturn(new GoogleUserInfo(
                        "id",
                        EMAIL,
                        "verified_email",
                        "name",
                        "given_name",
                        "family_name",
                        "picture",
                        "locale"));

        //when
        OAuthReadyResponse actual = memberService.getUserInfoFromOAuth(OAuthProvider.GOOGLE, "code-example");
        OAuthReadyResponse expected = OAuthReadyResponse.of(EMAIL, OAuthProvider.GOOGLE);

        //then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 oauth로 정보를 가져오면 에러가 발생한다.")
    void getUserInfoFromOAuthException() {
        //given
        given(googleRequester.supports(any(OAuthProvider.class)))
                .willReturn(true);
        given(googleRequester.getUserInfoByCode(anyString()))
                .willReturn(new GoogleUserInfo(
                        "id",
                        EMAIL,
                        "verified_email",
                        "name",
                        "given_name",
                        "family_name",
                        "picture",
                        "locale"));

        given(members.existsByEmail(EMAIL))
                .willReturn(true);

        //when, then
        assertThatThrownBy(() -> memberService.getUserInfoFromOAuth(OAuthProvider.GOOGLE, "code-example"))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"GOOGLE", "GITHUB"})
    @DisplayName("소셜 로그인을 이용해 회원가입할 수 있다.")
    void saveMemberByOAuth(String oauth) {
        //given
        OAuthMemberSaveRequest oAuthMemberSaveRequest = new OAuthMemberSaveRequest(EMAIL, ORGANIZATION, oauth);
        Member member = new Member(
                oAuthMemberSaveRequest.getEmail(),
                oAuthMemberSaveRequest.getOrganization(),
                oAuthMemberSaveRequest.getOauthProvider()
        );
        given(members.existsByEmail(anyString()))
                .willReturn(false);

        //when
        Member savedMember = new Member(
                1L,
                member.getEmail(),
                member.getOrganization(),
                member.getOAuthProvider());
        given(members.save(any(Member.class)))
                .willReturn(savedMember);

        //then
        MemberSaveResponse memberSaveResponse = MemberSaveResponse.from(savedMember);
        assertThat(memberService.saveMemberByOAuth(oAuthMemberSaveRequest)).usingRecursiveComparison()
                .isEqualTo(memberSaveResponse);
    }


    @ParameterizedTest
    @ValueSource(strings = {"GOOGLE", "GITHUB"})
    @DisplayName("이미 존재하는 이메일로 소셜 로그인을 이용해 회원가입하면 에러가 발생한다.")
    void saveMemberByOAuthException(String oauth) {
        //given
        OAuthMemberSaveRequest oAuthMemberSaveRequest = new OAuthMemberSaveRequest(EMAIL, ORGANIZATION, oauth);

        //when
        given(members.existsByEmail(anyString()))
                .willReturn(true);

        //then
        assertThatThrownBy(() -> memberService.saveMemberByOAuth(oAuthMemberSaveRequest))
                .isInstanceOf(DuplicateEmailException.class);
    }
}
