package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.SlackUrl;
import com.woowacourse.zzimkkong.dto.slack.Attachments;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Transactional(readOnly = true)
public class SlackService {
    private final SlackUrl slackUrl;
    private final WebClient slackWebClient;

    public SlackService(final SlackUrl slackUrl) {
        this.slackUrl = slackUrl;
        this.slackWebClient = WebClient.create(this.slackUrl.getUrl());
    }

    public void sendCreateMessage(SlackResponse slackResponse) {
        Attachments attachments = Attachments.createMessageFrom(slackResponse);
        send(attachments);
    }

    public void sendUpdateMessage(SlackResponse slackResponse) {
        Attachments attachments = Attachments.updateMessageFrom(slackResponse);
        send(attachments);
    }

    public void sendDeleteMessage(SlackResponse slackResponse) {
        Attachments attachments = Attachments.deleteMessageFrom(slackResponse);
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
