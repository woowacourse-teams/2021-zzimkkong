package com.woowacourse.zzimkkong.service.slack;

import com.woowacourse.zzimkkong.dto.slack.Attachments;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.service.slack.Sender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SlackService {
    private final String titleLink;
    private final List<Sender> senders;

    public SlackService(@Value("${service.url}") final String titleLink,
                        final List<Sender> senders) {
        this.titleLink = titleLink;
        this.senders = senders;
    }

    public void sendCreateMessage(SlackResponse slackResponse) {
        send(slackResponse, () -> Attachments.createMessageOf(slackResponse, titleLink));
    }

    public void sendUpdateMessage(SlackResponse slackResponse) {
        send(slackResponse, () -> Attachments.updateMessageOf(slackResponse, titleLink));
    }

    public void sendDeleteMessage(SlackResponse slackResponse) {
        send(slackResponse, () -> Attachments.deleteMessageOf(slackResponse, titleLink));
    }

    private void send(SlackResponse slackResponse, AttachmentsStrategy attachments) {
        Sender sender = senders.stream()
                .filter(s -> s.isSupport(slackResponse))
                .findAny()
                .orElseThrow();

        sender.send(attachments.getAttachments());
    }

    private interface AttachmentsStrategy {
        Attachments getAttachments();
    }
}
