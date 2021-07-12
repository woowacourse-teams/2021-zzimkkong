package com.woowacourse.zzimkkong.dto.member;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.woowacourse.zzimkkong.dto.Validator.*;

public class MemberSaveRequest {
    @NotBlank(message = EMPTY_MESSAGE)
    @Email(message = EMAIL_MESSAGE)
    private String email;

    @NotNull(message = EMPTY_MESSAGE)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,20}$", message = MEMBER_PASSWORD_MESSAGE)
    private String password;

    @NotNull(message = EMPTY_MESSAGE)
    @Pattern(regexp = "^[-_!?.,a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]{1,20}$", message = ORGANIZATION_MESSAGE)
    private String organization;

    public MemberSaveRequest() {
    }

    public MemberSaveRequest(final String email, final String password, final String organization) {
        this.email = email;
        this.password = password;
        this.organization = organization;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getOrganization() {
        return organization;
    }
}
