package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OAuthProvider;
import com.woowacourse.zzimkkong.domain.oauth.GoogleUserInfo;
import com.woowacourse.zzimkkong.domain.oauth.OAuthUserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static com.woowacourse.zzimkkong.infrastructure.oauth.GoogleRequesterTest.SALLY_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@ActiveProfiles("test")
class OAuthHandlerTest {
    @Autowired
    private OAuthHandler oauthHandler;

    @MockBean
    private GoogleRequester googleRequester;

    @Test
    @DisplayName("OAuth 제공사에 따라 적당한 OAuthRequester를 찾아 code로부터 유저 정보를 가져온다.")
    void getUserInfoFromCodeWithGithub() {
        // todo OAuthRequest를 mocking한다.
        OAuthUserInfo code = oauthHandler.getUserInfoFromCode(OAuthProvider.GITHUB, "code");

        String email = code.getEmail();
        // todo assert 구문
    }

    @Test
    @DisplayName("Google OAuth 인증")
    void getUserInfoFromCodeWithGoogle() {
        //given
        given(googleRequester.supports(any(OAuthProvider.class)))
                .willReturn(true);
        given(googleRequester.getUserInfoByCode(anyString()))
                .willReturn(new GoogleUserInfo(
                        "id",
                        SALLY_EMAIL,
                        "verified_email",
                        "name",
                        "given_name",
                        "family_name",
                        "picture",
                        "locale"));

        //when
        OAuthUserInfo userInfoFromCode = oauthHandler.getUserInfoFromCode(OAuthProvider.GOOGLE, "authorization_code_at_here");

        //then
        assertThat(userInfoFromCode.getEmail()).isEqualTo(SALLY_EMAIL);
    }
}
