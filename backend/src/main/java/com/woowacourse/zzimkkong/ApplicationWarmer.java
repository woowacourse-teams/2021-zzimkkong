package com.woowacourse.zzimkkong;

import com.woowacourse.zzimkkong.domain.SlackUrl;
import com.woowacourse.zzimkkong.infrastructure.thumbnail.BatikConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.*;

@Component
@Slf4j
@Profile("!test")
public class ApplicationWarmer implements ApplicationListener<ApplicationReadyEvent> {
    @Value("${s3proxy.server-uri}")
    private String s3ProxyServerUri;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        final ConfigurableApplicationContext applicationContext = applicationReadyEvent.getApplicationContext();

        log.info("warm up을 시작합니다");

        initSvgConverter(applicationContext);
        initWebClient(applicationContext);

        log.info("warm up이 완료 되었습니다.");
    }

    private void initSvgConverter(final ConfigurableApplicationContext applicationContext) {
        final BatikConverter batikConverter = applicationContext.getBean(BatikConverter.class);
        String svgData = "<?xml version=\"1.0\"?><svg fill=\"#000000\" xmlns=\"http://www.w3.org/2000/svg\"  viewBox=\"0 0 30 30\" width=\"30px\" height=\"30px\">    <path d=\"M 7 4 C 6.744125 4 6.4879687 4.0974687 6.2929688 4.2929688 L 4.2929688 6.2929688 C 3.9019687 6.6839688 3.9019687 7.3170313 4.2929688 7.7070312 L 11.585938 15 L 4.2929688 22.292969 C 3.9019687 22.683969 3.9019687 23.317031 4.2929688 23.707031 L 6.2929688 25.707031 C 6.6839688 26.098031 7.3170313 26.098031 7.7070312 25.707031 L 15 18.414062 L 22.292969 25.707031 C 22.682969 26.098031 23.317031 26.098031 23.707031 25.707031 L 25.707031 23.707031 C 26.098031 23.316031 26.098031 22.682969 25.707031 22.292969 L 18.414062 15 L 25.707031 7.7070312 C 26.098031 7.3170312 26.098031 6.6829688 25.707031 6.2929688 L 23.707031 4.2929688 C 23.316031 3.9019687 22.682969 3.9019687 22.292969 4.2929688 L 15 11.585938 L 7.7070312 4.2929688 C 7.5115312 4.0974687 7.255875 4 7 4 z\"/></svg>";

        try (final ByteArrayInputStream svgInputStream = new ByteArrayInputStream(svgData.getBytes());
             final ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream()) {
            final BufferedInputStream bufferedSvgInputStream = new BufferedInputStream(svgInputStream);
            final BufferedOutputStream bufferedPngOutputStream = new BufferedOutputStream(pngOutputStream);
            batikConverter.convertSvgToPng(bufferedSvgInputStream, bufferedPngOutputStream);
        } catch (IOException ignored) {
        }
    }

    private void initWebClient(final ConfigurableApplicationContext applicationContext) {
        final SlackUrl slackUrl = applicationContext.getBean(SlackUrl.class);
        final WebClient webClient = applicationContext.getBean(WebClient.class);

        webClient
                .get()
                .uri(slackUrl.getUrl())
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.just("warm up finisehd"))
                .subscribe();

        webClient
                .get()
                .uri(s3ProxyServerUri)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.just("warm up finished"))
                .block();
    }
}
