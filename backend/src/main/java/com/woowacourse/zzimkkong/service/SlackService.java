package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.dto.slack.Attachments;
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
    private final String URL_TEST = "https://hooks.slack.com/services/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";
    private final String URL = "https://hooks.slack.com/services/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";

    public void sendUpdateMessage(Reservation reservation) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Attachments attachments = Attachments.updateMessageFrom(reservation);
        HttpEntity<String> requestEntity = new HttpEntity<>(attachments.toString(), headers);

        restTemplate.exchange(URL, HttpMethod.POST, requestEntity, String.class);
    }

    public void sendDeleteMessage(Reservation reservation) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Attachments attachments = Attachments.deleteMessageFrom(reservation);
        HttpEntity<String> requestEntity = new HttpEntity<>(attachments.toString(), headers);

        restTemplate.exchange(URL, HttpMethod.POST, requestEntity, String.class);
    }
}
