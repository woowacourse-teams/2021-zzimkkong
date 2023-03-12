package com.woowacourse.zzimkkong.dto.member;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.OauthProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberFindResponse {
    private Long id;
    private String email;
    private String userName;
    private ProfileEmojiResponse emoji;
    private String organization;
    private OauthProvider oauthProvider;

    private MemberFindResponse(
            final Long id,
            final String email,
            final String userName,
            final ProfileEmojiResponse profileEmojiResponse,
            final String organization,
            final OauthProvider oauthProvider) {
        this.id = id;
        this.email = email;
        this.userName = userName;
        this.emoji = profileEmojiResponse;
        this.organization = organization;
        this.oauthProvider = oauthProvider;
    }

    public static MemberFindResponse from(final Member member) {
        return new MemberFindResponse(
                member.getId(),
                member.getEmail(),
                member.getUserName(),
                ProfileEmojiResponse.from(member.getEmoji()),
                member.getOrganization(),
                member.getOauthProvider());
    }
}
