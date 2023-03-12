package com.woowacourse.zzimkkong.dto.member;

import com.woowacourse.zzimkkong.domain.ProfileEmoji;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEmojiResponse {
    private ProfileEmoji name;
    private String code;

    public static ProfileEmojiResponse from(final ProfileEmoji emoji) {
        return ProfileEmojiResponse.builder()
                .name(emoji)
                .code(emoji.getCode())
                .build();
    }
}
