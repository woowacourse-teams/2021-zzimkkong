package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OAuthProvider;
import com.woowacourse.zzimkkong.exception.infrastructure.UnsupportedOAuthProviderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OAuthHandler {
    private final List<OAuthAPIRequester> OAuthAPIRequesters;

    @Autowired
    public OAuthHandler(List<OAuthAPIRequester> OAuthAPIRequesters) {
        this.OAuthAPIRequesters = OAuthAPIRequesters;
    }

    public OAuthUserInfo getUserInfoFromCode(OAuthProvider oauthProvider, String code) {
        OAuthAPIRequester requester = getRequester(oauthProvider);
        return requester.getUserInfoByCode(code);
    }

    private OAuthAPIRequester getRequester(OAuthProvider oauthProvider) {
        return OAuthAPIRequesters.stream()
                .filter(OAuthAPIRequester -> OAuthAPIRequester.supports(oauthProvider))
                .findFirst()
                .orElseThrow(UnsupportedOAuthProviderException::new);
    }
}
