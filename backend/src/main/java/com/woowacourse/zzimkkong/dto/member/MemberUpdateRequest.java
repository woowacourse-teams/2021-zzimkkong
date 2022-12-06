package com.woowacourse.zzimkkong.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.*;

@Getter
@NoArgsConstructor
public class MemberUpdateRequest {
    @NotNull(message = EMPTY_MESSAGE)
    @Pattern(regexp = ORGANIZATION_FORMAT, message = ORGANIZATION_MESSAGE)
    private String organization;

    @NotBlank(message = EMPTY_MESSAGE)
    @Pattern(regexp = NAMING_FORMAT, message = NAME_MESSAGE)
    private String userName;

    public MemberUpdateRequest(final String organization, final String userName) {
        this.organization = organization;
        this.userName = userName;
    }
}
