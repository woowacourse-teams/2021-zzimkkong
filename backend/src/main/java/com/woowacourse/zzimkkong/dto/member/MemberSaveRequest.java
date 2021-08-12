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
public class MemberSaveRequest {
    @NotBlank(message = EMPTY_MESSAGE)
    @Email(message = EMAIL_MESSAGE)
    private String email;

    @NotNull(message = EMPTY_MESSAGE)
    @Pattern(regexp = MEMBER_PW_FORMAT, message = MEMBER_PW_MESSAGE)
    private String password;

    @NotNull(message = EMPTY_MESSAGE)
    @Pattern(regexp = ORGANIZATION_FORMAT, message = ORGANIZATION_MESSAGE)
    private String organization;

    public MemberSaveRequest(final String email, final String password, final String organization) {
        this.email = email;
        this.password = password;
        this.organization = organization;
    }
}
