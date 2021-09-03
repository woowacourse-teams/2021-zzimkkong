package com.woowacourse.zzimkkong.infrastructure.oauth;

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
    @DisplayName("임시 발급 인증 ")
    void getUserInfoFromCode() {

        //
    }
}
