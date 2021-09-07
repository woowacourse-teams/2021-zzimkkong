package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OAuthProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class OAuthHandlerTest {
    @Autowired
    private OAuthHandler oauthHandler;

    @Test
    @DisplayName("Google OAuth 인증")
    void getUserInfoFromCode() {
        OAuthUserInfo userInfoFromCode = oauthHandler.getUserInfoFromCode(OAuthProvider.GOOGLE, "4%2F0AX4XfWh9nxibum5Wv-toYt89I5f0KxG5UcplVFbs_rcGMBkO8TJ27efucZX4KkyqyGtT_Q");
        assertThat(userInfoFromCode.getEmail()).isEqualTo("dusdn1702@gmail.com");
    }
}
