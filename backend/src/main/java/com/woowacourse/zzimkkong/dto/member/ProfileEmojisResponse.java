package com.woowacourse.zzimkkong.dto.member;

import com.woowacourse.zzimkkong.domain.ProfileEmoji;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ProfileEmojisResponse {
    private List<ProfileEmojiResponse> emojis = new ArrayList<>();

    public ProfileEmojisResponse(final List<ProfileEmojiResponse> emojis) {
        this.emojis = emojis;
    }

    public static ProfileEmojisResponse from(final List<ProfileEmoji> allEmojis) {
        List<ProfileEmojiResponse> emojisResponse = allEmojis.stream()
                .map(ProfileEmojiResponse::from)
                .collect(Collectors.toList());
        return new ProfileEmojisResponse(emojisResponse);
    }
}
