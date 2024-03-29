package com.woowacourse.zzimkkong.dto.member;

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
public class MemberSaveRequest {
    @NotBlank(message = EMPTY_MESSAGE)
    @Email(message = EMAIL_MESSAGE)
    private String email;

    @NotBlank(message = EMPTY_MESSAGE)
    @Pattern(regexp = NAMING_FORMAT, message = NAME_MESSAGE)
    private String userName;

    @NotNull(message = EMPTY_MESSAGE)
    private ProfileEmoji emoji;

    @NotNull(message = EMPTY_MESSAGE)
    @Pattern(regexp = MEMBER_PW_FORMAT, message = MEMBER_PW_MESSAGE)
    private String password;

    public MemberSaveRequest(final String email, final String userName, final ProfileEmoji emoji, final String password) {
        this.email = email;
        this.userName = userName;
        this.emoji = emoji;
        this.password = password;
    }
}
