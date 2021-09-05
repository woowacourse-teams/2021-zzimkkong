package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OAuthProvider;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:config/oauth.properties")
public interface OAuthAPIRequester {
    boolean supports(OAuthProvider oAuthProvider);

    OAuthUserInfo getUserInfoByCode(String code);
}
