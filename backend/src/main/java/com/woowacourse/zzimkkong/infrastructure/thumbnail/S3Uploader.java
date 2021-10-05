package com.woowacourse.zzimkkong.infrastructure.thumbnail;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.woowacourse.zzimkkong.exception.infrastructure.S3UploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class S3Uploader implements StorageUploader {
    private static final String S3_DOMAIN_FORMAT = "https://%s.s3.%s.amazonaws.com";
    private static final String PATH_DELIMITER = "/";

    private final AmazonS3 amazonS3;
    private final String bucketName;
    private final String s3DomainUrl;
    private final String urlReplacement;

    public S3Uploader(
            final AmazonS3 amazonS3,
            @Value("${aws.s3.bucket_name}") final String bucketName,
            @Value("${aws.s3.region}") final String regionName,
            @Value("${aws.s3.url_replacement}") final String urlReplacement) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
        this.s3DomainUrl = String.format(S3_DOMAIN_FORMAT, this.bucketName, regionName);
        this.urlReplacement = urlReplacement;
    }

    @Override
    public String upload(final String directoryName, final File uploadFile) {
        String fileName = directoryName + PATH_DELIMITER + uploadFile.getName();

        try {
            String resourceUrl = putS3(uploadFile, fileName);
            return replaceUrl(resourceUrl, urlReplacement);
        } catch (AmazonClientException exception) {
            throw new S3UploadException(exception);
        }
    }

    private String putS3(final File uploadFile, final String fileName) {
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, uploadFile));
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    private String replaceUrl(final String origin, final String replacement) {
        return origin.replace(s3DomainUrl, replacement);
    }

    @Override
    public void delete(String directoryName, final String fileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, directoryName + "/" + fileName));
    }
}
