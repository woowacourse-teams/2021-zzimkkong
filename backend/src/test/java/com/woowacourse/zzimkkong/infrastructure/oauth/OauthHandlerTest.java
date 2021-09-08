package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OauthProvider;
import com.woowacourse.zzimkkong.domain.oauth.GithubUserInfo;
import com.woowacourse.zzimkkong.domain.oauth.GoogleUserInfo;
import com.woowacourse.zzimkkong.domain.oauth.OauthUserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static com.woowacourse.zzimkkong.infrastructure.oauth.GoogleRequesterTest.SALLY_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@ActiveProfiles("test")
class OauthHandlerTest {
    @Autowired
    private OauthHandler oauthHandler;

    @MockBean
    private GoogleRequester googleRequester;

    @MockBean
    private GithubRequester githubRequester;

    @ParameterizedTest
    @DisplayName("Oauth 제공사에 따라 적당한 OauthRequester를 찾아 code로부터 유저 정보를 가져온다.")
    @EnumSource(OauthProvider.class)
    void getUserInfoFromCodeWithGithub(OauthProvider oauthProvider) {
        //given
        given(githubRequester.supports(OauthProvider.GITHUB))
                .willReturn(true);
        mockingGithubGetUserInfo(SALLY_EMAIL);

        given(googleRequester.supports(OauthProvider.GOOGLE))
                .willReturn(true);
        mockingGoogleGetUserInfo(SALLY_EMAIL);

        //when
        OauthUserInfo oauthUserInfo = oauthHandler.getUserInfoFromCode(oauthProvider, "code");
        String email = oauthUserInfo.getEmail();

        //then
        assertThat(email).isEqualTo(SALLY_EMAIL);
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
