package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.domain.ProfileEmoji;
import com.woowacourse.zzimkkong.dto.member.oauth.OauthMemberSaveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.*;
import static org.assertj.core.api.Assertions.assertThat;

class OauthMemberSaveRequestTest extends RequestTest {
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("oauth 회원가입 이메일에 빈 문자열이 들어오면 처리한다.")
    void blankEmail(String email) {
        OauthMemberSaveRequest oauthMemberSaveRequest = new OauthMemberSaveRequest(email, "sakjung", ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST, "ORGANIZTION", "GOOGLE");

        assertThat(getConstraintViolations(oauthMemberSaveRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"email:true", "email@email:false", "email@email.com:false"}, delimiter = ':')
    @DisplayName("oauth 회원가입 이메일에 옳지 않은 이메일 형식의 문자열이 들어오면 처리한다.")
    void invalidEmail(String email, boolean flag) {
        OauthMemberSaveRequest oauthMemberSaveRequest = new OauthMemberSaveRequest(email, "sakjung", ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST, "ORGANIZTION", "GOOGLE");

        assertThat(getConstraintViolations(oauthMemberSaveRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMAIL_MESSAGE)))
                .isEqualTo(flag);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("회원가입 조직명에 빈 문자열이 들어오면 처리한다.")
    void blankOrganization(String organization) {
        OauthMemberSaveRequest oauthMemberSaveRequest = new OauthMemberSaveRequest("email@email.com", "sakjung", ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST, organization, "GOOGLE");

        assertThat(getConstraintViolations(oauthMemberSaveRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"hihellomorethantwenty:true", "한글조직:false", "hihello:false", "안 녕 하 세 요:false", "ㄱㄴ 힣 ㄷㄹ:false"}, delimiter = ':')
    @DisplayName("회원가입 조직명에 옳지 않은 형식의 문자열이 들어오면 처리한다.")
    void invalidOrganization(String organization, boolean flag) {
        OauthMemberSaveRequest oauthMemberSaveRequest = new OauthMemberSaveRequest("email@email.com", "sakjung", ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST, organization, "GOOGLE");

        assertThat(getConstraintViolations(oauthMemberSaveRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(ORGANIZATION_MESSAGE)))
                .isEqualTo(flag);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("회원가입한 oauth 제공사에 빈 문자열이 들어오면 처리한다.")
    void blankOauthProvider(String oauthProvider) {
        OauthMemberSaveRequest oauthMemberSaveRequest = new OauthMemberSaveRequest("email@email.com",  "sakjung", ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST, "organization", oauthProvider);

        assertThat(getConstraintViolations(oauthMemberSaveRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }
}
