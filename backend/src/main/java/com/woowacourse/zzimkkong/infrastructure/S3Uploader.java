package com.woowacourse.zzimkkong.infrastructure;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class S3Uploader {
    private final AmazonS3 amazonS3;

    public S3Uploader(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Value("${aws.s3.bucket_name}")
    private String bucket;


    public String upload(File uploadFile) {
        String fileName = "thumbnails/" + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);

        uploadFile.delete();

        return uploadImageUrl;
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucket, fileName).toString();
    }
}
