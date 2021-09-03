package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OAuthProvider;
import org.springframework.stereotype.Component;

@Component
public class GoogleRequester implements OAuthAPIRequester {
    @Override
    public boolean supports(OAuthProvider oAuthProvider) {
        return false;
    }

    @Override
    public OAuthUserInfo getUserInfoByCode(String code) {
        return null;
    }
}
