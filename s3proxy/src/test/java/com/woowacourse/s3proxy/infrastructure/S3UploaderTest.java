package com.woowacourse.s3proxy.infrastructure;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Random;

import static com.woowacourse.s3proxy.Constants.LUTHER_IMAGE_URI_S3;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class S3UploaderTest {

    @Test
    @DisplayName("멀티 파트 파일을 업로드하고 URI를 얻어, 접근 가능한 URI(CloudFront)로 변경해 리턴한다.")
    void upload() throws IOException {
        // given
        AmazonS3 amazonS3 = mock(AmazonS3.class);
        String bucketName = "testBucketName";
        String cloudFrontUrl = "https://expectedCloudFrontUrl.net";

        S3Uploader s3Uploader = new S3Uploader(amazonS3, bucketName, cloudFrontUrl);

        given(amazonS3.getUrl(anyString(), anyString()))
                .willReturn(new URL(LUTHER_IMAGE_URI_S3));

        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        given(mockMultipartFile.getOriginalFilename())
                .willReturn("somePngFileName.png");
        given(mockMultipartFile.getSize())
                .willReturn(new Random().nextLong());
        given(mockMultipartFile.getInputStream())
                .willReturn(mock(InputStream.class));

        // when
        String directoryName = "testDirectoryName";
        URI actual = s3Uploader.upload(mockMultipartFile, directoryName);

        String resourceUriWithoutHost = LUTHER_IMAGE_URI_S3.split("amazonaws.com")[1];
        String expectedUri = cloudFrontUrl + resourceUriWithoutHost;

        // then
        assertThat(actual).isEqualTo(URI.create(expectedUri));
    }

    @Test
    @DisplayName("경로를 입력받아 파일을 삭제할 수 있다.")
    void delete() {
        // given
        AmazonS3 amazonS3 = mock(AmazonS3.class);
        String bucketName = "testBucketName";
        String cloudFrontUrl = "https://testCloudFrontUrl.net";

        S3Uploader s3Uploader = new S3Uploader(amazonS3, bucketName, cloudFrontUrl);

        String fileName = "filename.png";
        String directory = "directoryName";

        // when, then
        s3Uploader.delete(directory + "/" + fileName);
    }
}
