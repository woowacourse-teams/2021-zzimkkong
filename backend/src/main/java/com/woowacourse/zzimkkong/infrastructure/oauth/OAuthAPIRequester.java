package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OAuthProvider;
import com.woowacourse.zzimkkong.domain.oauth.OAuthUserInfo;

public interface OAuthAPIRequester {
    boolean supports(OAuthProvider oAuthProvider);

    OAuthUserInfo getUserInfoByCode(String code);
}
