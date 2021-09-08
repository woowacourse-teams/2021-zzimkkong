package com.woowacourse.zzimkkong.dto.member;

import com.woowacourse.zzimkkong.domain.OAuthProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuthReadyResponse {
    private String email;
    private OAuthProvider oauthProvider;

    private OAuthReadyResponse(final String email, final OAuthProvider oAuthProvider) {
        this.email = email;
        this.oauthProvider = oAuthProvider;
    }

    public static OAuthReadyResponse of(final String email, final OAuthProvider oAuthProvider) {
        return new OAuthReadyResponse(email, oAuthProvider);
    }

    public OAuthProvider getOAuthProvider() {
        return oauthProvider;
    }
}
