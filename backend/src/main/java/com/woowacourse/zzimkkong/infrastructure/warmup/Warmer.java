package com.woowacourse.zzimkkong.infrastructure.warmup;

import com.woowacourse.zzimkkong.domain.SlackUrl;
import com.woowacourse.zzimkkong.infrastructure.thumbnail.BatikConverter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.*;

@Slf4j
public class Warmer {
    private final BatikConverter batikConverter;
    private final SlackUrl slackUrl;
    private final WebClient webClient;
    private final String s3ProxyServerUri;
    private final String s3ProxyServerSecretKey;

    public Warmer(
            final BatikConverter batikConverter,
            final SlackUrl slackUrl,
            final WebClient webClient,
            final String s3ProxyServerUri,
            final String s3ProxyServerSecretKey) {
        this.batikConverter = batikConverter;
        this.slackUrl = slackUrl;
        this.webClient = webClient;
        this.s3ProxyServerUri = s3ProxyServerUri;
        this.s3ProxyServerSecretKey = s3ProxyServerSecretKey;
    }

    public void warmUp() {
        log.info("warm up을 시작합니다");

        initSvgConverter();
        initWebClient();

        log.info("warm up이 완료 되었습니다.");
    }

    private void initSvgConverter() {
        String svgData = "<?xml version=\"1.0\"?><svg fill=\"#000000\" xmlns=\"http://www.w3.org/2000/svg\"  viewBox=\"0 0 30 30\" width=\"30px\" height=\"30px\">    <path d=\"M 7 4 C 6.744125 4 6.4879687 4.0974687 6.2929688 4.2929688 L 4.2929688 6.2929688 C 3.9019687 6.6839688 3.9019687 7.3170313 4.2929688 7.7070312 L 11.585938 15 L 4.2929688 22.292969 C 3.9019687 22.683969 3.9019687 23.317031 4.2929688 23.707031 L 6.2929688 25.707031 C 6.6839688 26.098031 7.3170313 26.098031 7.7070312 25.707031 L 15 18.414062 L 22.292969 25.707031 C 22.682969 26.098031 23.317031 26.098031 23.707031 25.707031 L 25.707031 23.707031 C 26.098031 23.316031 26.098031 22.682969 25.707031 22.292969 L 18.414062 15 L 25.707031 7.7070312 C 26.098031 7.3170312 26.098031 6.6829688 25.707031 6.2929688 L 23.707031 4.2929688 C 23.316031 3.9019687 22.682969 3.9019687 22.292969 4.2929688 L 15 11.585938 L 7.7070312 4.2929688 C 7.5115312 4.0974687 7.255875 4 7 4 z\"/></svg>";

        try (final ByteArrayInputStream svgInputStream = new ByteArrayInputStream(svgData.getBytes());
             final ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream()) {
            final BufferedInputStream bufferedSvgInputStream = new BufferedInputStream(svgInputStream);
            final BufferedOutputStream bufferedPngOutputStream = new BufferedOutputStream(pngOutputStream);
            batikConverter.convertSvgToPng(bufferedSvgInputStream, bufferedPngOutputStream);
        } catch (IOException ignored) {
        }
    }

    private void initWebClient() {
        webClient
                .post()
                .uri(slackUrl.getUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("let's warm up")
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.just("warm up finisehd"))
                .subscribe();

        try (final BufferedInputStream bufferedPngInputStream = new BufferedInputStream(new ByteArrayInputStream("warmUp".getBytes()))) {
            MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
            multipartBodyBuilder.part("file", new InputStreamResource(bufferedPngInputStream))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=file; filename=warmFile");

            webClient
                    .post()
                    .uri(s3ProxyServerUri + "/api/storage/warmup")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, s3ProxyServerSecretKey)
                    .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorResume(e -> Mono.just("warm up finished"))
                    .block();
        } catch (IOException ignored) {
        }
    }
}
