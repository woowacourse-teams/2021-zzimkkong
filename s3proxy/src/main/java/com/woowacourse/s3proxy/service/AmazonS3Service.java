package com.woowacourse.s3proxy.service;

import com.woowacourse.s3proxy.infrastructure.S3Uploader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@Slf4j
@Service
public class AmazonS3Service {
    private final S3Uploader s3Uploader;

    public AmazonS3Service(S3Uploader s3Uploader) {
        this.s3Uploader = s3Uploader;
    }

    public URI upload(MultipartFile multipartFile, String directoryPath) {
        return s3Uploader.upload(multipartFile, directoryPath);
    }
}
