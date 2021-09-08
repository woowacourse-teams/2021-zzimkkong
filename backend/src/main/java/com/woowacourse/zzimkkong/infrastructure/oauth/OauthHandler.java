package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OauthProvider;
import com.woowacourse.zzimkkong.domain.oauth.OauthUserInfo;
import com.woowacourse.zzimkkong.exception.infrastructure.UnsupportedOauthProviderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OauthHandler {
    private final List<OauthAPIRequester> oauthAPIRequesters;

    @Autowired
    public OauthHandler(final List<OauthAPIRequester> oauthAPIRequesters) {
        this.oauthAPIRequesters = oauthAPIRequesters;
    }

    public OauthUserInfo getUserInfoFromCode(final OauthProvider oauthProvider, final String code) {
        OauthAPIRequester requester = getRequester(oauthProvider);
        return requester.getUserInfoByCode(code);
    }

    private OauthAPIRequester getRequester(final OauthProvider oauthProvider) {
        return oauthAPIRequesters.stream()
                .filter(requester -> requester.supports(oauthProvider))
                .findFirst()
                .orElseThrow(UnsupportedOauthProviderException::new);
    }
}
