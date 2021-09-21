package com.woowacourse.s3proxy.service;

import com.woowacourse.s3proxy.infrastructure.S3Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

import static com.woowacourse.s3proxy.Constants.LUTHER_IMAGE_URI_CLOUDFRONT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class S3ServiceTest extends ServiceTest {
    @MockBean
    private S3Uploader s3Uploader;

    @Autowired
    private S3Service s3Service;

    @BeforeEach
    void mockingS3Uploader() {
        given(s3Uploader.upload(any(MultipartFile.class), anyString()))
                .willReturn(URI.create(LUTHER_IMAGE_URI_CLOUDFRONT));
    }

    @Test
    @DisplayName("멀티파트로 전송된 파일을 요청한 디렉토리에 업로드한다.")
    void upload() {
        // given
        MultipartFile multipartFile = mock(MultipartFile.class);

        // when
        URI actual = s3Service.upload(multipartFile, "thumbnails");

        // then
        assertThat(actual).isEqualTo(URI.create(LUTHER_IMAGE_URI_CLOUDFRONT));
    }

    @Test
    @DisplayName("스토리지의 파일을 삭제할 수 있다.")
    void delete() {
        // given
        String fileName = "filename.png";
        String directory = "directoryName";

        // when, then
        s3Service.delete(directory, fileName);
    }
}
