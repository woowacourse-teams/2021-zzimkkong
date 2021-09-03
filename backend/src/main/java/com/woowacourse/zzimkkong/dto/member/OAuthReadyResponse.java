package com.woowacourse.zzimkkong.dto.member;

import com.woowacourse.zzimkkong.domain.OAuthProvider;
import lombok.Getter;

@Getter
public class OAuthReadyResponse {
    private String email;
    private OAuthProvider oAuthProvider;

    public OAuthReadyResponse(String email, OAuthProvider oAuthProvider) {
        this.email = email;
        this.oAuthProvider = oAuthProvider;
    }
}
