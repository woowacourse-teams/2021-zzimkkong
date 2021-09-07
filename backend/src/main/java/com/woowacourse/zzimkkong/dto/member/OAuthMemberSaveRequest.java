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
public class OAuthMemberSaveRequest {
    @NotBlank(message = EMPTY_MESSAGE)
    @Email(message = EMAIL_MESSAGE)
    private String email;

    @NotNull(message = EMPTY_MESSAGE)
    @Pattern(regexp = ORGANIZATION_FORMAT, message = ORGANIZATION_MESSAGE)
    private String organization;

    @NotNull(message = EMPTY_MESSAGE)
    private String oAuthProvider;

    public OAuthMemberSaveRequest(final String email,
                                  final String organization,
                                  final String oAuthProvider) {
        this.email = email;
        this.organization = organization;
        this.oAuthProvider = oAuthProvider;
    }
}
