package com.woowacourse.zzimkkong.infrastructure;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.woowacourse.zzimkkong.exception.S3UploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class S3Uploader {
    private final AmazonS3 amazonS3;
    private final String bucket;

    public S3Uploader(final AmazonS3 amazonS3, @Value("${aws.s3.bucket_name}") final String bucket) {
        this.amazonS3 = amazonS3;
        this.bucket = bucket;
    }

    public String upload(final String directoryName, final File uploadFile) {
        String fileName = directoryName + "/" + uploadFile.getName();

        try {
            return putS3(uploadFile, fileName);
        } catch (AmazonClientException e) {
            throw new S3UploadException();
        }
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile));
        return amazonS3.getUrl(bucket, fileName).toString();
    }
}
