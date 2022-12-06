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
    void blankEmail(String email) {
        MemberSaveRequest memberSaveRequest = new MemberSaveRequest(email, "sakjung", "password1", "organization");

        assertThat(getConstraintViolations(memberSaveRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"email:true", "email@email:false", "email@email.com:false"}, delimiter = ':')
    @DisplayName("회원가입 이메일에 옳지 않은 이메일 형식의 문자열이 들어오면 처리한다.")
    void invalidEmail(String email, boolean flag) {
        MemberSaveRequest memberSaveRequest = new MemberSaveRequest(email, "sakjung","password1", "organization");

        assertThat(getConstraintViolations(memberSaveRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMAIL_MESSAGE)))
                .isEqualTo(flag);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("회원가입 비밀번호 빈 문자열이 들어오면 처리한다.")
    void blankPassword(String password) {
        MemberSaveRequest memberSaveRequest = new MemberSaveRequest("email@email.com", "sakjung", password, "organization");

        assertThat(getConstraintViolations(memberSaveRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"test1234!:false", "test1234:false", "1234test:false", "testtest:true", "12341234:true", "test123:true", "test1234test1234test1:true", "한글도실패1231:true"}, delimiter = ':')
    @DisplayName("회원가입 비밀번호에 옳지 않은 비밀번호 형식의 문자열이 들어오면 처리한다.")
    void invalidPassword(String password, boolean flag) {
        MemberSaveRequest memberSaveRequest = new MemberSaveRequest("email@email.com", "sakjung", password, "organization");

        assertThat(getConstraintViolations(memberSaveRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(MEMBER_PW_MESSAGE)))
                .isEqualTo(flag);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("회원가입 조직명에 빈 문자열이 들어오면 처리한다.")
    void blankOrganization(String organization) {
        MemberSaveRequest memberSaveRequest = new MemberSaveRequest("email@email.com", "sakjung", "password", organization);

        assertThat(getConstraintViolations(memberSaveRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"hihellomorethantwenty:true", "한글조직:false", "hihello:false", "안 녕 하 세 요:false", "ㄱㄴ 힣 ㄷㄹ:false"}, delimiter = ':')
    @DisplayName("회원가입 조직명에 옳지 않은 형식의 문자열이 들어오면 처리한다.")
    void invalidOrganization(String organization, boolean flag) {
        MemberSaveRequest memberSaveRequest = new MemberSaveRequest("email@email.com", "sakjung", "password", organization);

        assertThat(getConstraintViolations(memberSaveRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(ORGANIZATION_MESSAGE)))
                .isEqualTo(flag);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("회원가입 유저 이름에 빈 문자열이 들어오면 처리한다.")
    void blankUserName(String userName) {
        MemberSaveRequest memberSaveRequest = new MemberSaveRequest("email@email.com", userName, "password", "organization");

        assertThat(getConstraintViolations(memberSaveRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"hihellomorethantwenty:true", "허용되지않은놈$#@:true", "안 녕 하 세 요:true", "한글조직:false", "hihello:false", "ㄱㄴ힣ㄷㄹ:false"}, delimiter = ':')
    @DisplayName("회원가입 유저 이름에 옳지 않은 형식의 문자열이 들어오면 처리한다.")
    void invalidUserName(String userName, boolean flag) {
        MemberSaveRequest memberSaveRequest = new MemberSaveRequest("email@email.com", userName, "password", "organization");

        assertThat(getConstraintViolations(memberSaveRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(NAME_MESSAGE)))
                .isEqualTo(flag);
    }
}
