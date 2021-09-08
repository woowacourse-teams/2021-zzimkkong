package com.woowacourse.zzimkkong.dto.member;

import com.woowacourse.zzimkkong.domain.OAuthProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuthReadyResponse {
    private String email;
    private OAuthProvider oauthProvider;

    private OAuthReadyResponse(final String email, final OAuthProvider oauthProvider) {
        this.email = email;
        this.oauthProvider = oauthProvider;
    }

    public static OAuthReadyResponse of(final String email, final OAuthProvider oauthProvider) {
        return new OAuthReadyResponse(email, oauthProvider);
    }

    public OAuthProvider getOAuthProvider() {
        return oauthProvider;
    }
}
