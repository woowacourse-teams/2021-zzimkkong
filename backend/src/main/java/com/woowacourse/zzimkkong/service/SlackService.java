package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.dto.slack.Attachments;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class SlackService {
    private final WebClient slackWebClient;
    private final String titleLink;

    public SlackService(@Value("${service.url}") final String titleLink,
                        final WebClient webClient) {
        this.titleLink = titleLink;
        ConnectionProvider provider = ConnectionProvider.builder("slack-pool")
                .maxConnections(10)
                .maxIdleTime(Duration.ofSeconds(2L))
                .maxLifeTime(Duration.ofSeconds(2L))
                .lifo()
                .build();
        HttpClient httpClient = HttpClient.create(provider);
        slackWebClient = webClient.mutate().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

    public void sendCreateMessage(SlackResponse slackResponse) {
        Attachments attachments = Attachments.createMessageOf(slackResponse, titleLink);
        send(attachments, slackResponse.getSlackUrl());
    }

    public void sendUpdateMessage(SlackResponse slackResponse) {
        Attachments attachments = Attachments.updateMessageOf(slackResponse, titleLink);
        send(attachments, slackResponse.getSlackUrl());
    }

    public void sendDeleteMessage(SlackResponse slackResponse) {
        Attachments attachments = Attachments.deleteMessageOf(slackResponse, titleLink);
        send(attachments, slackResponse.getSlackUrl());
    }

    private void send(final Attachments attachments, final String slackUrl) {
        if (!Objects.isNull(slackUrl)) {
            slackWebClient.post()
                    .uri(slackUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(attachments.toString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .then()
                    .subscribe();
        }
    }
}
