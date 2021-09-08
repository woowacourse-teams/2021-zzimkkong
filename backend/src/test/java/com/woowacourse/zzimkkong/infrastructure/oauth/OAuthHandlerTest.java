package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OAuthProvider;
import com.woowacourse.zzimkkong.domain.oauth.GithubUserInfo;
import com.woowacourse.zzimkkong.domain.oauth.GoogleUserInfo;
import com.woowacourse.zzimkkong.domain.oauth.OAuthUserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

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

    @MockBean
    private GithubRequester githubRequester;

    @ParameterizedTest
    @DisplayName("OAuth 제공사에 따라 적당한 OAuthRequester를 찾아 code로부터 유저 정보를 가져온다.")
    @EnumSource(OAuthProvider.class)
    void getUserInfoFromCodeWithGithub(OAuthProvider oAuthProvider) {
        given(githubRequester.supports(OAuthProvider.GITHUB))
                .willReturn(true);
        mockingGithubGetUserInfo(SALLY_EMAIL);
        given(googleRequester.supports(OAuthProvider.GOOGLE))
                .willReturn(true);
        mockingGoogleGetUserInfo(SALLY_EMAIL);

        OAuthUserInfo oAuthUserInfo = oauthHandler.getUserInfoFromCode(oAuthProvider, "code");
        String email = oAuthUserInfo.getEmail();

        assertThat(email).isEqualTo(SALLY_EMAIL);
    }

    @Test
    @DisplayName("Google OAuth 인증")
    void getUserInfoFromCodeWithGoogle() {
        //given
        given(googleRequester.supports(any(OAuthProvider.class)))
                .willReturn(true);
        mockingGoogleGetUserInfo(SALLY_EMAIL);

        //when
        OAuthUserInfo userInfoFromCode = oauthHandler.getUserInfoFromCode(OAuthProvider.GOOGLE, "authorization_code_at_here");

        //then
        assertThat(userInfoFromCode.getEmail()).isEqualTo(SALLY_EMAIL);
    }

    private void mockingGithubGetUserInfo(String email) {
        given(githubRequester.getUserInfoByCode(anyString()))
                .willReturn(GithubUserInfo.from(Map.of("email", email)));
    }

    private void mockingGoogleGetUserInfo(String email) {
        given(googleRequester.getUserInfoByCode(anyString()))
                .willReturn(new GoogleUserInfo(
                        "id",
                        email,
                        "verified_email",
                        "name",
                        "given_name",
                        "family_name",
                        "picture",
                        "locale"));
    }
}
