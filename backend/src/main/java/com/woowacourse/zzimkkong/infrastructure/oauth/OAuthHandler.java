package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OAuthProvider;
import com.woowacourse.zzimkkong.domain.oauth.OAuthUserInfo;
import com.woowacourse.zzimkkong.exception.infrastructure.UnsupportedOAuthProviderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OAuthHandler {
    private final List<OAuthAPIRequester> oAuthAPIRequesters;

    @Autowired
    public OAuthHandler(List<OAuthAPIRequester> oAuthAPIRequesters) {
        this.oAuthAPIRequesters = oAuthAPIRequesters;
    }

    public OAuthUserInfo getUserInfoFromCode(OAuthProvider oAuthProvider, String code) {
        OAuthAPIRequester requester = getRequester(oAuthProvider);
        return requester.getUserInfoByCode(code);
    }

    private OAuthAPIRequester getRequester(OAuthProvider oAuthProvider) {
        return oAuthAPIRequesters.stream()
                .filter(requester -> requester.supports(oAuthProvider))
                .findFirst()
                .orElseThrow(UnsupportedOAuthProviderException::new);
    }
}
