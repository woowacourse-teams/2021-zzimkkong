package com.woowacourse.zzimkkong.infrastructure.oauth;

import com.woowacourse.zzimkkong.Constants;
import com.woowacourse.zzimkkong.domain.oauth.OauthUserInfo;
import com.woowacourse.zzimkkong.exception.infrastructure.oauth.ErrorResponseToGetAccessTokenException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
class GithubRequesterTest {

    private static final String ACCESS_TOKEN_RESPONSE_EXAMPLE = "{\n" +
            "    \"access_token\": \"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\",\n" +
            "    \"token_type\": \"bearer\",\n" +
            "    \"scope\": \"user:email\"\n" +
            "}";

    private static final String USER_INFO_RESPONSE_EXAMPLE = "{\n" +
            "    \"login\": \"pobi\",\n" +
            "    \"id\": 1,\n" +
            "    \"node_id\": \"MDQ6VXNlcjQ5MzQ2Njc3\",\n" +
            "    \"avatar_url\": \"https://avatars.githubusercontent.com/u/49346677?v=4\",\n" +
            "    \"gravatar_id\": \"\",\n" +
            "    \"url\": \"https://api.github.com/users/tributetothemoon\",\n" +
            "    \"html_url\": \"https://github.com/tributetothemoon\",\n" +
            "    \"followers_url\": \"https://api.github.com/users/tributetothemoon/followers\",\n" +
            "    \"following_url\": \"https://api.github.com/users/tributetothemoon/following{/other_user}\",\n" +
            "    \"gists_url\": \"https://api.github.com/users/tributetothemoon/gists{/gist_id}\",\n" +
            "    \"starred_url\": \"https://api.github.com/users/tributetothemoon/starred{/owner}{/repo}\",\n" +
            "    \"subscriptions_url\": \"https://api.github.com/users/tributetothemoon/subscriptions\",\n" +
            "    \"organizations_url\": \"https://api.github.com/users/tributetothemoon/orgs\",\n" +
            "    \"repos_url\": \"https://api.github.com/users/tributetothemoon/repos\",\n" +
            "    \"events_url\": \"https://api.github.com/users/tributetothemoon/events{/privacy}\",\n" +
            "    \"received_events_url\": \"https://api.github.com/users/tributetothemoon/received_events\",\n" +
            "    \"type\": \"User\",\n" +
            "    \"site_admin\": false,\n" +
            "    \"name\": \"Jaesung Park\",\n" +
            "    \"company\": \"@woowacourse\",\n" +
            "    \"blog\": \"woowacourse.github.io\",\n" +
            "    \"location\": \"Seoul, Korea\",\n" +
            "    \"email\": \"pobi@email.com\",\n" +
            "    \"hireable\": null,\n" +
            "    \"bio\": null,\n" +
            "    \"twitter_username\": null,\n" +
            "    \"public_repos\": 27,\n" +
            "    \"public_gists\": 0,\n" +
            "    \"followers\": 300000,\n" +
            "    \"following\": 1,\n" +
            "    \"created_at\": \"2019-04-06T16:39:37Z\",\n" +
            "    \"updated_at\": \"2021-09-05T07:23:40Z\"\n" +
            "}";

    @Test
    @DisplayName("서버에 요청을 보내 코드로부터 유저의 정보를 가져온다.")
    void getUserInfoByCode() {
        try (MockWebServer mockGithubServer = new MockWebServer()) {
            // given
            mockGithubServer.start();

            setUpGetTokenResponse(mockGithubServer, ACCESS_TOKEN_RESPONSE_EXAMPLE);
            setUpGetTokenResponse(mockGithubServer, USER_INFO_RESPONSE_EXAMPLE);

            GithubRequester githubRequester = new GithubRequester(
                    "clientId",
                    "secretId",
                    String.format("http://%s:%s", mockGithubServer.getHostName(), mockGithubServer.getPort()),
                    String.format("http://%s:%s", mockGithubServer.getHostName(), mockGithubServer.getPort()),
                    WebClient.create()
            );

            // when
            OauthUserInfo code = githubRequester.getUserInfoByCode("code");
            String email = code.getEmail();

            // then
            assertThat(email).isEqualTo(Constants.EMAIL);
            mockGithubServer.shutdown();
        } catch (IOException ignored) {
        }
    }

    @Test
    @DisplayName("오류가 발생하면 ErrorResponseToGetGithubAccessTokenException을 응답한다.")
    void getTokenException() {
        String getTokenErrorResponse = "{\n" +
                "  \"error\": \"bad_verification_code\",\n" +
                "  \"error_description\": \"The code passed is incorrect or expired.\",\n" +
                "  \"error_uri\": \"/apps/managing-oauth-apps/troubleshooting-oauth-app-access-token-request-errors/#bad-verification-code\"\n" +
                "}";

        try (MockWebServer mockGithubServer = new MockWebServer()) {
            // given
            mockGithubServer.start();

            setUpGetTokenResponse(mockGithubServer, getTokenErrorResponse);
            setUpGetTokenResponse(mockGithubServer, USER_INFO_RESPONSE_EXAMPLE);

            GithubRequester githubRequester = new GithubRequester(
                    "clientId",
                    "secretId",
                    String.format("http://%s:%s", mockGithubServer.getHostName(), mockGithubServer.getPort()),
                    String.format("http://%s:%s", mockGithubServer.getHostName(), mockGithubServer.getPort()),
                    WebClient.create()
            );

            // when, then
            assertThatThrownBy(() -> githubRequester.getUserInfoByCode("code"))
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
