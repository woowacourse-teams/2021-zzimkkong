package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.dto.member.LoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.*;
import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest extends RequestTest {
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("로그인 이메일에 빈 문자열이 들어오면 처리한다.")
    void blankEmail(String email) {
        LoginRequest loginRequest = new LoginRequest(email, "password1");

        assertThat(getConstraintViolations(loginRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"email:true", "email@email:false", "email@email.com:false"}, delimiter = ':')
    @DisplayName("로그인 이메일에 옳지 않은 이메일 형식의 문자열이 들어오면 처리한다.")
    void invalidEmail(String email, boolean flag) {
        LoginRequest loginRequest = new LoginRequest(email, "password1");

        assertThat(getConstraintViolations(loginRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMAIL_MESSAGE)))
                .isEqualTo(flag);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("로그인 비밀번호 빈 문자열이 들어오면 처리한다.")
    void blankPassword(String password) {
        LoginRequest loginRequest = new LoginRequest("email@email.com", password);

        assertThat(getConstraintViolations(loginRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"test1234:false", "1234test:false", "testtest:true", "12341234:true", "test123:true", "test1234test1234test1:true", "test1234!:true", "한글도실패1231:true"}, delimiter = ':')
    @DisplayName("로그인 비밀번호에 옳지 않은 비밀번호 형식의 문자열이 들어오면 처리한다.")
    void invalidPassword(String password, boolean flag) {
        LoginRequest loginRequest = new LoginRequest("email@email.com", password);

        assertThat(getConstraintViolations(loginRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(MEMBER_PW_MESSAGE)))
                .isEqualTo(flag);
    }
}
