package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.domain.OAuthProvider;
import com.woowacourse.zzimkkong.domain.oauth.OAuthUserInfo;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class GoogleRequesterTest {
    public static final String SALLY_EMAIL = "dusdn1702@gmail.com";

    private static final String ACCESS_TOKEN_RESPONSE_EXAMPLE = "{\n" +
            "    \"access_token\": \"ACCESS_TOKEN_AT_HERE\",\n" +
            "    \"expires_in\": 3599,\n" +
            "    \"refresh_token\": \"REFRESH_TOKEN_AT_HERE\",\n" +
            "    \"scope\": \"https://www.googleapis.com/auth/drive.metadata.readonly https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/indexing openid https://www.googleapis.com/auth/userinfo.email\",\n" +
            "    \"token_type\": \"Bearer\",\n" +
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
    void getUserInfoByCode() {
        try (MockWebServer mockGoogleServer = new MockWebServer()) {
            mockGoogleServer.start();

            setUpResponse(mockGoogleServer);

            GoogleRequester googleRequester = new GoogleRequester(
                    "clientId",
                    "secretId",
                    "http://localhost:8080",
                    String.format("http://%s:%s", mockGoogleServer.getHostName(), mockGoogleServer.getPort()),
                    String.format("http://%s:%s", mockGoogleServer.getHostName(), mockGoogleServer.getPort())
            );

            OAuthUserInfo code = googleRequester.getUserInfoByCode("code");
            String email = code.getEmail();

            assertThat(email).isEqualTo(SALLY_EMAIL);

            mockGoogleServer.shutdown();
        } catch (IOException ignored) {
        }
    }

    @Test
    void supports() {
        GoogleRequester googleRequester = new GoogleRequester(
                "clientId",
                "secretId",
                "http://localhost:8080",
                "baseLoginUri",
                "baseUserUri"
        );

        assertThat(googleRequester.supports(OAuthProvider.GOOGLE)).isTrue();
    }

    private void setUpResponse(MockWebServer mockGithubServer) {
        mockGithubServer.enqueue(new MockResponse()
                .setBody(ACCESS_TOKEN_RESPONSE_EXAMPLE)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        mockGithubServer.enqueue(new MockResponse()
                .setBody(USER_INFO_RESPONSE_EXAMPLE)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    }
}
