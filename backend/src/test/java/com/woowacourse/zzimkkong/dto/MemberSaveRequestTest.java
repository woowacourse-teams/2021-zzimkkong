package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.dto.member.MemberSaveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.*;
import static org.assertj.core.api.Assertions.assertThat;

class MemberSaveRequestTest extends RequestTest {
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("회원가입 이메일에 빈 문자열이 들어오면 처리한다.")
    public void blankEmail(String email) {
        MemberSaveRequest memberSaveRequest = new MemberSaveRequest(email, "password1", "organization");

        assertThat(getConstraintViolations(memberSaveRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"email:true", "email@email:false", "email@email.com:false"}, delimiter = ':')
    @DisplayName("회원가입 이메일에 옳지 않은 이메일 형식의 문자열이 들어오면 처리한다.")
    public void invalidEmail(String email, boolean flag) {
        MemberSaveRequest memberSaveRequest = new MemberSaveRequest(email, "password1", "organization");

        assertThat(getConstraintViolations(memberSaveRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMAIL_MESSAGE)))
                .isEqualTo(flag);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("회원가입 비밀번호 빈 문자열이 들어오면 처리한다.")
    public void blankPassword(String password) {
        MemberSaveRequest memberSaveRequest = new MemberSaveRequest("email@email.com", password, "organization");

        assertThat(getConstraintViolations(memberSaveRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"test1234:false", "testtest:true", "test123:true", "test1234test1234test1:true"}, delimiter = ':')
    //todo: 특수문자 거르기 "test1234!:true",
    @DisplayName("회원가입 비밀번호에 옳지 않은 비밀번호 형식의 문자열이 들어오면 처리한다.")
    public void invalidPassword(String password, boolean flag) {
        MemberSaveRequest memberSaveRequest = new MemberSaveRequest("email@email.com", password, "organization");

        assertThat(getConstraintViolations(memberSaveRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(MEMBER_PASSWORD_MESSAGE)))
                .isEqualTo(flag);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("회원가입 조직명에 빈 문자열이 들어오면 처리한다.")
    public void blankOrganization(String organization) {
        MemberSaveRequest memberSaveRequest = new MemberSaveRequest("email@email.com", "password", organization);

        assertThat(getConstraintViolations(memberSaveRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"hihellomorethantwenty:true", "한글조직:false", "hihello:false"}, delimiter = ':')
    @DisplayName("회원가입 조직명에 옳지 않은 형식의 문자열이 들어오면 처리한다.")
    public void invalidOrganization(String organization, boolean flag) {
        MemberSaveRequest memberSaveRequest = new MemberSaveRequest("email@email.com", "password", organization);

        assertThat(getConstraintViolations(memberSaveRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(ORGANIZATION_MESSAGE)))
                .isEqualTo(flag);
    }
}
