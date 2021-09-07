package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OAuthProvider;
import com.woowacourse.zzimkkong.domain.oauth.OAuthUserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class OAuthHandlerTest {
    @Autowired
    private OAuthHandler oauthHandler;

    @Test
    @DisplayName("OAuth 제공사에 따라 적당한 OAuthRequester를 찾아 code로부터 유저 정보를 가져온다.")
    void getUserInfoFromCode() {
        // todo OAuthRequest를 mocking한다.
        OAuthUserInfo code = oauthHandler.getUserInfoFromCode(OAuthProvider.GITHUB, "code");

        String email = code.getEmail();
        // todo assert 구문
    }
}
