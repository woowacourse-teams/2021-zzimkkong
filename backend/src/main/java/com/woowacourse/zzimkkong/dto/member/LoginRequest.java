package com.woowacourse.zzimkkong.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.*;

@Getter
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = EMPTY_MESSAGE)
    @Email(message = EMAIL_MESSAGE)
    private String email;

    @NotNull(message = EMPTY_MESSAGE)
    @Pattern(regexp = MEMBER_PASSWORD_FORMAT, message = MEMBER_PASSWORD_MESSAGE)
    private String password;

    public LoginRequest(final String email, final String password) {
        this.email = email;
        this.password = password;
    }
}
