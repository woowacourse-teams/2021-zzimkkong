package com.woowacourse.zzimkkong.infrastructure;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

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
    @DisplayName("업로드된 파일을 삭제한다.")
    void delete() {
        String filePath = getClass().getClassLoader().getResource("luther.png").getFile();
        testFile = new File(filePath);

        String uri = s3ProxyUploader.upload("testDirectoryName", testFile);

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

    @AfterEach
    void tearDown() {
        s3ProxyUploader.delete(testDirectoryName, testFile.getName());
    }
}
