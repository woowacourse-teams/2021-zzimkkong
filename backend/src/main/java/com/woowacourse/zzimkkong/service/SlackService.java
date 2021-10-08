package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.SlackUrl;
import com.woowacourse.zzimkkong.dto.slack.Attachments;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional(readOnly = true)
public class SlackService {
    private final SlackUrl slackUrl;
    private final RestTemplate restTemplate;
    private final HttpHeaders headers;

    public SlackService(final SlackUrl slackUrl) {
        this.slackUrl = slackUrl;
        restTemplate = new RestTemplate();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    public void sendCreateMessage(SlackResponse slackResponse) {
        Attachments attachments = Attachments.createMessageFrom(slackResponse);
        HttpEntity<String> requestEntity = new HttpEntity<>(attachments.toString(), headers);

        restTemplate.exchange(slackUrl.getUrl(), HttpMethod.POST, requestEntity, String.class);
    }

    public void sendUpdateMessage(SlackResponse slackResponse) {
        Attachments attachments = Attachments.updateMessageFrom(slackResponse);
        HttpEntity<String> requestEntity = new HttpEntity<>(attachments.toString(), headers);

        restTemplate.exchange(slackUrl.getUrl(), HttpMethod.POST, requestEntity, String.class);
    }

    public void sendDeleteMessage(SlackResponse slackResponse) {
        Attachments attachments = Attachments.deleteMessageFrom(slackResponse);
        HttpEntity<String> requestEntity = new HttpEntity<>(attachments.toString(), headers);

        restTemplate.exchange(slackUrl.getUrl(), HttpMethod.POST, requestEntity, String.class);
    }
}
