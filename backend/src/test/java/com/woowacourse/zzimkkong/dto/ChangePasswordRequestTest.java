package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.dto.member.ChangePasswordRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

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
}