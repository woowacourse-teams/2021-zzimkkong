package com.woowacourse.s3proxy.infrastructure;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.woowacourse.s3proxy.exception.S3UploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@Component
public class S3Uploader {
    private static final String S3_DOMAIN_FORMAT = "https://%s.s3.%s.amazonaws.com";
    private static final String PATH_DELIMITER = "/";

    private final AmazonS3 amazonS3;
    private final String bucketName;
    private final String s3DomainUrl;
    private final String urlReplacement;

    public S3Uploader(
            final AmazonS3 amazonS3,
            @Value("${aws.s3.bucket-name}") final String bucketName,
            @Value("${aws.s3.region}") final String regionName,
            @Value("${aws.s3.mapped-cloudfront}") final String cloudFrontUrl) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
        this.s3DomainUrl = String.format(S3_DOMAIN_FORMAT, this.bucketName, regionName);
        this.urlReplacement = cloudFrontUrl;
    }

    public URI upload(MultipartFile multipartFile, String directoryPath) {
        ObjectMetadata objectMetadata = createObjectMetadata(multipartFile);

        String fileName = multipartFile.getOriginalFilename();
        String fileFullPath = generateFullPath(directoryPath, fileName);

        try {
            InputStream inputStream = multipartFile.getInputStream();

            amazonS3.putObject(this.bucketName, fileFullPath, inputStream, objectMetadata);

            URL fileUrl = amazonS3.getUrl(this.bucketName, directoryPath);

            return replaceUrl(fileUrl, urlReplacement);
        } catch (AmazonClientException | IOException exception) {
            throw new S3UploadException(exception);
        }
    }

    private ObjectMetadata createObjectMetadata(MultipartFile multipartFile) {
        String filename = multipartFile.getOriginalFilename();
        MediaType mediaType = MediaTypeFactory.getMediaType(filename)
                .orElseThrow(); // todo 사용자 정의 예외 throw

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(mediaType.toString());
        objectMetadata.setContentLength(multipartFile.getSize());

        return objectMetadata;
    }

    private String generateFullPath(String directoryPath, String fileName) {
        return directoryPath + PATH_DELIMITER + fileName;
    }

    private URI replaceUrl(final URL origin, final String replacement) {
        String replacedUrl = origin.toString().replace(s3DomainUrl, replacement);
        return URI.create(replacedUrl);
    }

    public void delete(final String fullPathOfFile) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fullPathOfFile));
    }
}
