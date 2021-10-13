package com.woowacourse.zzimkkong.infrastructure.thumbnail;

import com.woowacourse.zzimkkong.exception.infrastructure.S3ProxyRespondedFailException;
import com.woowacourse.zzimkkong.exception.infrastructure.S3UploadException;
import com.woowacourse.zzimkkong.infrastructure.thumbnail.S3ProxyUploader;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ActiveProfiles("test")
class S3ProxyUploaderTest {
    private static final String URL_REGEX = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    @Autowired
    private S3ProxyUploader s3ProxyUploader;

    private final String testDirectoryName = "testDirectoryName";
    private File testFile;

    @Test
    @DisplayName("파일을 업로드한 후 url을 받아온다.")
    void upload() {
        // given
        String filePath = getClass().getClassLoader().getResource("luther.png").getFile();
        testFile = new File(filePath);

        // when
        String uri = s3ProxyUploader.upload(testDirectoryName, testFile);
        Matcher matcher = URL_PATTERN.matcher(uri);

        // then
        assertThat(matcher.find()).isTrue();
    }

    @Test
    @DisplayName("데이터를 업로드한 후 파일의 url을 받아온다.")
    void uploadInputStreamData() throws FileNotFoundException {
        // given
        final String fileName = "luther.png";
        String filePath = getClass().getClassLoader().getResource(fileName).getFile();
        testFile = new File(filePath);

        final FileInputStream fileInputStream = new FileInputStream(testFile);

        // when
        String uri = s3ProxyUploader.upload(testDirectoryName, fileName, fileInputStream);
        Matcher matcher = URL_PATTERN.matcher(uri);

        // then
        assertThat(matcher.find()).isTrue();
    }

    @Test
    @DisplayName("업로드된 파일을 삭제한다.")
    void delete() throws FileNotFoundException {
        final String fileName = "luther.png";
        String filePath = getClass().getClassLoader().getResource(fileName).getFile();
        testFile = new File(filePath);

        final FileInputStream fileInputStream = new FileInputStream(testFile);

        String uri = s3ProxyUploader.upload(testDirectoryName, fileName, fileInputStream);

        // when
        s3ProxyUploader.delete(testDirectoryName, testFile.getName());
        RestAssured.port = RestAssured.UNDEFINED_PORT;
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept("application/json")
                .when().get(uri)
                .then().log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("업로드시 서버측이 201 응답을 보내지 않으면 예외가 발생한다.")
    void invalidUrl() {
        // given
        try(MockWebServer mockGithubServer = new MockWebServer()) {
            mockGithubServer.start();
            mockGithubServer.enqueue(new MockResponse()
                    .setResponseCode(400));

            String hostName = mockGithubServer.getHostName();
            int port = mockGithubServer.getPort();
            S3ProxyUploader s3ProxyUploader = new S3ProxyUploader("http://" + hostName + ":" + port);
            String filePath = getClass().getClassLoader().getResource("luther.png").getFile();
            testFile = new File(filePath);

            // when, then
            assertThatThrownBy(() -> s3ProxyUploader.upload("testdir", testFile))
                    .isInstanceOf(S3ProxyRespondedFailException.class);
        } catch (IOException ignored) {
        }
    }

    @Test
    @DisplayName("업로드할 파일의 정보를 읽어오던 중 IOException이 발생하면 예외를 발생시킨다.")
    void ioException() {
        // given
        File mockFile = mock(File.class);
        BDDMockito.given(mockFile.toPath())
                .willReturn(Path.of("/ubuntu/home/invalidPath.png"));

        // when
        assertThatThrownBy(() -> s3ProxyUploader.upload("testdir", mockFile))
                .isInstanceOf(S3UploadException.class);
    }

    @AfterEach
    void tearDown() {
        if (testFile != null) {
//            s3ProxyUploader.delete(testDirectoryName, testFile.getName());
            testFile = null;
        }
    }
}
