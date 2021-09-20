package com.woowacourse.s3proxy.controller;

import com.woowacourse.s3proxy.infrastructure.S3Uploader;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URI;

import static com.woowacourse.s3proxy.Constants.LUTHER_IMAGE_URI_CLOUDFRONT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

class S3ProxyControllerTest extends AcceptanceTest {
    @MockBean
    S3Uploader s3Uploader;

    @BeforeEach
    void setUp() {
        given(s3Uploader.upload(any(MultipartFile.class), anyString()))
                .willReturn(URI.create(LUTHER_IMAGE_URI_CLOUDFRONT));
    }

    @Test
    @DisplayName("스토리지에 파일을 업로드한다.")
    void upload() {
        // given
        String directory = "thumbnails";
        String filePath = getClass().getClassLoader().getResource("luther.png").getFile();
        File file = new File(filePath);

        // when
        ExtractableResponse<Response> response = uploadFile(directory, file);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private ExtractableResponse<Response> uploadFile(String directory, File file) {
        return RestAssured.given()
                .log().all()
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("file", file)
                .when().post("/api/storage/" + directory)
                .then().log().all().extract();
    }
}
