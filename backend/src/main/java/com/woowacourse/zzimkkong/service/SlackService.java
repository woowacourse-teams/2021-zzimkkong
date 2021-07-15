package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.dto.slack.Attachments;
import com.woowacourse.zzimkkong.dto.slack.Channel;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;

@Service
@Transactional
public class SlackService {
    private static final String URL_PREFIX = "https://hooks.slack.com/services/";

    private ReservationRepository reservations;

    public SlackService(final ReservationRepository reservations) {
        this.reservations = reservations;
    }

    public void sendUpdateMessage(SlackResponse slackResponse) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Attachments attachments = Attachments.updateMessageFrom(slackResponse);
        HttpEntity<String> requestEntity = new HttpEntity<>(attachments.toString(), headers);

        restTemplate.exchange(URL_PREFIX + Channel.LOCAL.url(), HttpMethod.POST, requestEntity, String.class);
    }

    public void sendDeleteMessage(SlackResponse slackResponse) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Attachments attachments = Attachments.deleteMessageFrom(slackResponse);
        HttpEntity<String> requestEntity = new HttpEntity<>(attachments.toString(), headers);

        restTemplate.exchange(URL_PREFIX + Channel.LOCAL.url(), HttpMethod.POST, requestEntity, String.class);
    }
}
