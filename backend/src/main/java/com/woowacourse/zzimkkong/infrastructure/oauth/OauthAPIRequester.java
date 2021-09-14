package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OauthProvider;
import com.woowacourse.zzimkkong.domain.oauth.OauthUserInfo;

public interface OauthAPIRequester {
    boolean supports(OauthProvider oauthProvider);

    OauthUserInfo getUserInfoByCode(String code);
}
