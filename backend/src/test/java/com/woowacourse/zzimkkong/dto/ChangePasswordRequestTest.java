package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.dto.member.ChangePasswordRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.EMPTY_MESSAGE;
import static com.woowacourse.zzimkkong.dto.ValidatorMessage.MEMBER_PW_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;

class ChangePasswordRequestTest extends RequestTest {
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("기존 비밀번호 확인란에 빈 문자열이 들어오면 처리한다.")
    void blankOldPassword(String oldPassword) {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(oldPassword, "newPassword", "newPasswordConfirm");

        assertThat(getConstraintViolations(changePasswordRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(MEMBER_PW_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("새 비밀번호란에 빈 문자열이 들어오면 처리한다.")
    void blankNewPassword(String newPassword) {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("oldPassword", newPassword, "newPasswordConfirm");

        assertThat(getConstraintViolations(changePasswordRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(MEMBER_PW_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("새 비밀번호 확인란에 빈 문자열이 들어오면 처리한다.")
    void blankNewPasswordConfirm(String newPasswordConfirm) {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("oldPassword", "newPassword", newPasswordConfirm);

        assertThat(getConstraintViolations(changePasswordRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(MEMBER_PW_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("비밀번호 변경시 새 비밀번호 란에 빈 문자열이 들어오면 처리한다.")
    void blankPassword(String password) {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("oldPassword1234", password, password);

        assertThat(getConstraintViolations(changePasswordRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"test1234!:false", "test1234:false", "1234test:false", "testtest:true", "12341234:true", "test123:true", "test1234test1234test1:true", "한글도실패1231:true"}, delimiter = ':')
    @DisplayName("비밀번호 변경시 새 비밀번호 란에 옳지 않은 비밀번호 형식의 문자열이 들어오면 처리한다.")
    void invalidPassword(String password, boolean flag) {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("oldPassword1234", password, password);

        assertThat(getConstraintViolations(changePasswordRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(MEMBER_PW_MESSAGE)))
                .isEqualTo(flag);
    }
}