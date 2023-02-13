package com.woowacourse.zzimkkong.dto.member;

import com.woowacourse.zzimkkong.domain.ProfileEmoji;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.*;

@Getter
@NoArgsConstructor
public class MemberUpdateRequest {
    @NotBlank(message = EMPTY_MESSAGE)
    @Pattern(regexp = NAMING_FORMAT, message = NAME_MESSAGE)
    private String userName;

    @NotNull(message = EMPTY_MESSAGE)
    private ProfileEmoji emoji;

    public MemberUpdateRequest(final String userName, final ProfileEmoji emoji) {
        this.userName = userName;
        this.emoji = emoji;
    }
}
