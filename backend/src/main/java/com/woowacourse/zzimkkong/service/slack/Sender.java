package com.woowacourse.zzimkkong.service.slack;

import com.woowacourse.zzimkkong.dto.slack.Attachments;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.infrastructure.sharingid.SharingIdGenerator;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

public abstract class Sender {
    protected static final Long LUTHER_ID = 7L;
    protected final SharingIdGenerator sharingIdGenerator;
    private final WebClient slackWebClient;

    protected Sender(final WebClient webClient,
                     final SharingIdGenerator sharingIdGenerator) {
        this.slackWebClient = webClient;
        this.sharingIdGenerator = sharingIdGenerator;
    }

    public abstract boolean isSupport(SlackResponse slackResponse);

    public void send(Attachments attachments) {
        slackWebClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(attachments.toString())
                .retrieve()
                .bodyToMono(String.class)
                .then()
                .subscribe();
    }
}
