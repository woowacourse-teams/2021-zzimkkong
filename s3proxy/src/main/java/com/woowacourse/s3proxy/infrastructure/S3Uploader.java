package com.woowacourse.s3proxy.infrastructure;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.woowacourse.s3proxy.exception.S3UploadException;
import com.woowacourse.s3proxy.exception.UnsupportedFileExtensionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

@Component
public class S3Uploader {
    private static final String S3_HOST_URL_SUFFIX = "amazonaws.com";
    private static final String PATH_DELIMITER = "/";
    public static final int RESOURCE_URL_INDEX = 1;

    private final AmazonS3 amazonS3;
    private final String bucketName;
    private final String cloudFrontUrl;

    public S3Uploader(
            final AmazonS3 amazonS3,
            @Value("${aws.s3.bucket-name}") final String bucketName,
            @Value("${aws.s3.mapped-cloudfront}") final String cloudFrontUrl) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
        this.cloudFrontUrl = cloudFrontUrl;
    }

    public URI upload(MultipartFile multipartFile, String directoryPath) {
        ObjectMetadata objectMetadata = createObjectMetadata(multipartFile);

        String fileName = multipartFile.getOriginalFilename();
        String fileFullPath = generateFullPath(directoryPath, fileName);

        try {
            InputStream inputStream = multipartFile.getInputStream();

            amazonS3.putObject(this.bucketName, fileFullPath, inputStream, objectMetadata);

            URL fileUrl = amazonS3.getUrl(this.bucketName, directoryPath);

            return makeAccessibleUrl(fileUrl, cloudFrontUrl);
        } catch (AmazonClientException | IOException exception) {
            throw new S3UploadException(exception);
        }
    }

    private ObjectMetadata createObjectMetadata(MultipartFile multipartFile) {
        String filename = multipartFile.getOriginalFilename();
        MediaType mediaType = MediaTypeFactory.getMediaType(filename)
                .orElseThrow(UnsupportedFileExtensionException::new);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(mediaType.toString());
        objectMetadata.setContentLength(multipartFile.getSize());

        return objectMetadata;
    }

    private String generateFullPath(String directoryPath, String fileName) {
        return directoryPath + PATH_DELIMITER + fileName;
    }

    private URI makeAccessibleUrl(final URL origin, final String cloudFrontUrl) {
        String uriWithoutHost = origin.toString().split(S3_HOST_URL_SUFFIX)[RESOURCE_URL_INDEX];
        String replacedUrl = cloudFrontUrl + uriWithoutHost;
        return URI.create(replacedUrl);
    }

    public void delete(final String fullPathOfFile) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fullPathOfFile));
    }
}
