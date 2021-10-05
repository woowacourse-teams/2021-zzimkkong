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
import static com.woowacourse.s3proxy.DocumentUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

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
        String directory = "testdir";
        String filePath = getClass().getClassLoader().getResource("luther.png").getFile();
        File file = new File(filePath);

        // when
        ExtractableResponse<Response> response = uploadFile(directory, file);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("스토리지의 파일을 삭제한다.")
    void delete() {
        // given
        String fileName = "filename.png";
        String directory = "testdir";

        // when
        ExtractableResponse<Response> response = deleteFile(directory, fileName);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> uploadFile(String directory, File file) {
        return RestAssured.given(getRequestSpecification())
                .log().all()
                .filter(document(
                        "s3/post", getRequestPreprocessor(), getResponsePreprocessor(),
                        pathParameters(parameterWithName("directory").description("저장하고자 하는 스토리지 내의 디렉토리 이름"))))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("file", file)
                .pathParam("directory", directory)
                .when().post("/api/storage/{directory}")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> deleteFile(String directory, String fileName) {
        return RestAssured.given(getRequestSpecification())
                .log().all()
                .filter(document("s3/delete", getRequestPreprocessor(), getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("directory").description("저장하고자 하는 스토리지 내의 디렉토리 이름"),
                                parameterWithName("filename").description("삭제하고자 하는 파일의 이름(확장자 포함)"))))
                .when()
                .pathParam("directory", directory)
                .pathParam("filename", fileName)
                .delete("/api/storage/{directory}/{filename}")
                .then().log().all().extract();
    }
}
