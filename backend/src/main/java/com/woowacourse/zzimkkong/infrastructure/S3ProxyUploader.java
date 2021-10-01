package com.woowacourse.zzimkkong.infrastructure;

import com.woowacourse.zzimkkong.exception.infrastructure.S3ProxyRespondedFailException;
import com.woowacourse.zzimkkong.exception.infrastructure.S3UploadException;
import com.woowacourse.zzimkkong.infrastructure.thumbnail.StorageUploader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

@Component
public class S3ProxyUploader implements StorageUploader {
    private static final String PATH_DELIMETER = "/";
    private static final String API_PATH = "/api/storage";
    private static final String CONTENT_DISPOSITION_HEADER_VALUE_FORMAT = "form-data; name=file; filename=%s";

    private final WebClient proxyServerClient;

    public S3ProxyUploader(
            @Value("${s3proxy.server-uri}") final String serverUri) {
        this.proxyServerClient = WebClient.builder()
                .baseUrl(serverUri)
                .build();
    }

    @Override
    public String upload(String directoryName, File uploadFile) {
        try {
            byte[] byteArrayOfFile = Files.readAllBytes(uploadFile.toPath());

            MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
            multipartBodyBuilder.part("file", new ByteArrayResource(byteArrayOfFile))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            String.format(CONTENT_DISPOSITION_HEADER_VALUE_FORMAT, uploadFile.getName()));

            return proxyServerClient
                    .method(HttpMethod.POST)
                    .uri(String.join(PATH_DELIMETER, API_PATH, directoryName))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                    .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                    .exchangeToMono(clientResponse -> {
                        if (clientResponse.statusCode().equals(HttpStatus.CREATED)) {
                            String location = Objects.requireNonNull(
                                            clientResponse.headers().asHttpHeaders().get(HttpHeaders.LOCATION))
                                    .stream().findFirst()
                                    .orElseThrow(S3UploadException::new);
                            return Mono.just(location);
                        }
                        return Mono.error(S3ProxyRespondedFailException::new);
                    })
                    .block();
        } catch (IOException exception) {
            throw new S3UploadException(exception);
        }
    }

    @Override
    public void delete(String directoryName, String fileName) {
        proxyServerClient
                .method(HttpMethod.DELETE)
                .uri(String.join(PATH_DELIMETER, API_PATH, directoryName, fileName))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
