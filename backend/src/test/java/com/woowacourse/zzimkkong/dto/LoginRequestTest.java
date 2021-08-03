package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.dto.member.LoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.EMAIL_MESSAGE;
import static com.woowacourse.zzimkkong.dto.ValidatorMessage.EMPTY_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest extends RequestTest {
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("로그인 이메일에 빈 문자열이 들어오면 처리한다.")
    public void blankEmail(String email) {
        LoginRequest loginRequest = new LoginRequest(email, "password");

        assertThat(getConstraintViolations(loginRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"email:true", "email@email:false", "email@email.com:false"}, delimiter = ':')
    @DisplayName("로그인 이메일에 옳지 않은 이메일 형식의 문자열이 들어오면 처리한다.")
    public void invalidEmail(String email, boolean flag) {
        LoginRequest loginRequest = new LoginRequest(email, "password");

        assertThat(getConstraintViolations(loginRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMAIL_MESSAGE)))
                .isEqualTo(flag);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("로그인 비밀번호 빈 문자열이 들어오면 처리한다.")
    public void blankPassword(String password) {
        LoginRequest loginRequest = new LoginRequest("email@email.com", password);

        assertThat(getConstraintViolations(loginRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"email:true", "email@email:false", "email@email.com:false"}, delimiter = ':')
    @DisplayName("로그인 비밀번호에 옳지 않은 비밀번호 형식의 문자열이 들어오면 처리한다.")
    public void invalidPassword(String email, boolean flag) {
        LoginRequest loginRequest = new LoginRequest(email, "password");

        assertThat(getConstraintViolations(loginRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMAIL_MESSAGE)))
                .isEqualTo(flag);
    }
}
