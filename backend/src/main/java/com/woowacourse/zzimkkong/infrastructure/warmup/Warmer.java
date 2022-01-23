package com.woowacourse.zzimkkong.infrastructure.warmup;

import com.woowacourse.zzimkkong.domain.SlackUrl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class Warmer {
    private final SlackUrl slackUrl;
    private final WebClient webClient;

    public Warmer(
            final SlackUrl slackUrl,
            final WebClient webClient) {
        this.slackUrl = slackUrl;
        this.webClient = webClient;
    }

    public void warmUp() {
        log.info("warm up을 시작합니다");

        initWebClient();

        log.info("warm up이 완료 되었습니다.");
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
    }
}
