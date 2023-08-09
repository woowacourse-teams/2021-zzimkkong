package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.dto.slack.Attachments;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class SlackService {
    private final WebClient slackWebClient;
    private final String titleLink;

    public SlackService(@Value("${service.url}") final String titleLink,
                        final WebClient webClient) {
        this.titleLink = titleLink;
        slackWebClient = webClient;
    }

    public void sendCreateMessage(SlackResponse slackResponse) {
        Attachments attachments = Attachments.createMessageOf(slackResponse, titleLink);
        send(attachments, slackResponse.getSlackUrl());
    }

    public void sendUpdateMessage(SlackResponse slackResponse) {
        Attachments attachments = Attachments.updateMessageOf(slackResponse, titleLink);
        send(attachments, slackResponse.getSlackUrl());
    }

    public void sendEarlyStopMessage(SlackResponse slackResponse) {
        Attachments attachments = Attachments.earlyStopMessageOf(slackResponse, titleLink);
        send(attachments, slackResponse.getSlackUrl());
    }

    public void sendDeleteMessage(SlackResponse slackResponse) {
        Attachments attachments = Attachments.deleteMessageOf(slackResponse, titleLink);
        send(attachments, slackResponse.getSlackUrl());
    }

    private void send(final Attachments attachments, final String slackUrl) {
        if (!Objects.isNull(slackUrl)) {
            slackWebClient.mutate()
                    .baseUrl(slackUrl)
                    .build()
                    .post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(attachments.toString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .then()
                    .subscribe();
        }
    }
}
