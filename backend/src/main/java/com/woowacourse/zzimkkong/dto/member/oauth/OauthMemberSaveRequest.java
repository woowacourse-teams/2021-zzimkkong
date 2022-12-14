package com.woowacourse.zzimkkong.dto.member.oauth;

import com.woowacourse.zzimkkong.domain.ProfileEmoji;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.*;

@Getter
@NoArgsConstructor
public class OauthMemberSaveRequest {
    @NotBlank(message = EMPTY_MESSAGE)
    @Email(message = EMAIL_MESSAGE)
    private String email;

    @NotBlank(message = EMPTY_MESSAGE)
    @Pattern(regexp = NAMING_FORMAT, message = NAME_MESSAGE)
    private String userName;

    @NotNull(message = EMPTY_MESSAGE)
    private ProfileEmoji emoji;

    @NotNull(message = EMPTY_MESSAGE)
    @Pattern(regexp = ORGANIZATION_FORMAT, message = ORGANIZATION_MESSAGE)
    private String organization;

    @NotNull(message = EMPTY_MESSAGE)
    private String oauthProvider;

    public OauthMemberSaveRequest(final String email,
                                  final String userName,
                                  final ProfileEmoji emoji,
                                  final String organization,
                                  final String oauthProvider) {
        this.email = email;
        this.userName = userName;
        this.emoji = emoji;
        this.organization = organization;
        this.oauthProvider = oauthProvider;
    }
}
