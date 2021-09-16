package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OauthProvider;
import com.woowacourse.zzimkkong.domain.oauth.OauthUserInfo;
import com.woowacourse.zzimkkong.exception.infrastructure.oauth.ErrorResponseToGetAccessTokenException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
class GoogleRequesterTest {
    public static final String SALLY_EMAIL = "dusdn1702@gmail.com";
    private static final String GOOGLE_TOKEN_RESPONSE = "{\n" +
            "    \"access_token\": \"ACCESS_TOKEN_AT_HERE\",\n" +
            "    \"expires_in\": \"3599\",\n" +
            "    \"refresh_token\": \"REFRESH_TOKEN_AT_HERE\",\n" +
            "    \"scope\": \"https://www.googleapis.com/auth/drive.metadata.readonly https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/indexing openid https://www.googleapis.com/auth/userinfo.email\",\n" +
            "    \"token_type\": \"bearer\",\n" +
            "    \"id_token\": \"ID_TOKEN_AT_HERE\"\n" +
            "}";

    private static final String USER_INFO_RESPONSE_EXAMPLE = "{\n" +
            "    \"id\": \"107677594285931275665\",\n" +
            "    \"email\": \"" + SALLY_EMAIL + "\",\n" +
            "    \"verified_email\": true,\n" +
            "    \"name\": \"Yeonwoo Cho\",\n" +
            "    \"given_name\": \"Yeonwoo\",\n" +
            "    \"family_name\": \"Cho\",\n" +
            "    \"picture\": \"https://lh3.googleusercontent.com/a/AATXAJyyWzN0hxXPXy_hoPNk7ww9Kuu990o-ImGrdPe9=s96-c\",\n" +
            "    \"locale\": \"ko\"\n" +
            "}";

    @Test
    @DisplayName("서버에 요청을 보내 코드로부터 유저의 정보를 가져온다.")
    void getUserInfoByCode() {
        try (MockWebServer mockGoogleServer = new MockWebServer()) {
            // given
            mockGoogleServer.start();

            setUpResponse(mockGoogleServer);

            GoogleRequester googleRequester = new GoogleRequester(
                    "clientId",
                    "secretId",
                    "redirectUri",
                    String.format("http://%s:%s", mockGoogleServer.getHostName(), mockGoogleServer.getPort()),
                    String.format("http://%s:%s", mockGoogleServer.getHostName(), mockGoogleServer.getPort())
            );

            // when
            OauthUserInfo code = googleRequester.getUserInfoByCode("code");
            String email = code.getEmail();

            //then
            assertThat(email).isEqualTo(SALLY_EMAIL);
            mockGoogleServer.shutdown();
        } catch (IOException ignored) {
        }
    }

    @Test
    @DisplayName("들어온 oauth 제공자가 자신이면 true를 반환한다.")
    void supports() {
        GoogleRequester googleRequester = new GoogleRequester(
                "clientId",
                "secretId",
                "redirectUri",
                "baseLoginUri",
                "baseUserUri"
        );

        assertThat(googleRequester.supports(OauthProvider.GOOGLE)).isTrue();
        assertThat(googleRequester.supports(OauthProvider.GITHUB)).isFalse();
    }

    private void setUpResponse(MockWebServer mockGithubServer) {
        mockGithubServer.enqueue(new MockResponse()
                .setBody(GOOGLE_TOKEN_RESPONSE)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        mockGithubServer.enqueue(new MockResponse()
                .setBody(USER_INFO_RESPONSE_EXAMPLE)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    }

    @Value("${google.uri.redirect}")
    private String redirectUri;

    @Test
    @DisplayName("오류가 발생하면 ErrorResponseToGetGithubAccessTokenException을 응답한다.")
    void getTokenException() {
        String getTokenErrorResponse = "{\n" +
                "    \"error\": \"redirect_uri_mismatch\",\n" +
                "    \"error_description\": \"Bad Request\"\n" +
                "}";

        try (MockWebServer mockGithubServer = new MockWebServer()) {
            // given
            mockGithubServer.start();

            setUpGetTokenResponse(mockGithubServer, getTokenErrorResponse);
            setUpGetTokenResponse(mockGithubServer, USER_INFO_RESPONSE_EXAMPLE);

            GoogleRequester googleRequester = new GoogleRequester(
                    "clientId",
                    "secretId",
                    this.redirectUri,
                    String.format("http://%s:%s", mockGithubServer.getHostName(), mockGithubServer.getPort()),
                    String.format("http://%s:%s", mockGithubServer.getHostName(), mockGithubServer.getPort())
            );

            // when, then
            assertThatThrownBy(() -> googleRequester.getUserInfoByCode("code"))
                    .isInstanceOf(ErrorResponseToGetAccessTokenException.class);
        } catch (IOException ignored) {
        }
    }

    private void setUpGetTokenResponse(MockWebServer mockGithubServer, String responseExample) {
        mockGithubServer.enqueue(new MockResponse()
                .setBody(responseExample)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    }
}
