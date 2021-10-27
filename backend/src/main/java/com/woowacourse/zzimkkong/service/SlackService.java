package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.SlackUrl;
import com.woowacourse.zzimkkong.dto.slack.Attachments;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Transactional(readOnly = true)
public class SlackService {
    private final WebClient slackWebClient;
    private final String titleLink;

    public SlackService(@Value("${service.url}") String titleLink,
                        final SlackUrl slackUrl,
                        final WebClient webClient) {
        this.titleLink = titleLink;
        this.slackWebClient = webClient.mutate()
                .baseUrl(slackUrl.getUrl())
                .build();
    }

    public void sendCreateMessage(SlackResponse slackResponse) {
        Attachments attachments = Attachments.createMessageOf(slackResponse, titleLink);
        send(attachments);
    }

    public void sendUpdateMessage(SlackResponse slackResponse) {
        Attachments attachments = Attachments.updateMessageOf(slackResponse, titleLink);
        send(attachments);
    }

    public void sendDeleteMessage(SlackResponse slackResponse) {
        Attachments attachments = Attachments.deleteMessageOf(slackResponse, titleLink);
        send(attachments);
    }

    private void send(final Attachments attachments) {
        slackWebClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(attachments.toString())
                .retrieve()
                .bodyToMono(String.class)
                .then()
                .subscribe();
    }
}
