package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OAuthProvider;

public interface OAuthAPIRequester {
    boolean supports(OAuthProvider oAuthProvider);

    OAuthUserInfo getUserInfoByCode(String code);
}
