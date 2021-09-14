package com.woowacourse.zzimkkong.dto.member.oauth;

import com.woowacourse.zzimkkong.domain.OauthProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OauthReadyResponse {
    private String email;
    private OauthProvider oauthProvider;

    private OauthReadyResponse(final String email, final OauthProvider oauthProvider) {
        this.email = email;
        this.oauthProvider = oauthProvider;
    }

    public static OauthReadyResponse of(final String email, final OauthProvider oauthProvider) {
        return new OauthReadyResponse(email, oauthProvider);
    }
}
