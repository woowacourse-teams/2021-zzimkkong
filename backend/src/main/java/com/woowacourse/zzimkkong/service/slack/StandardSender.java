package com.woowacourse.zzimkkong.service.slack;

import com.woowacourse.zzimkkong.domain.SlackUrl;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.infrastructure.sharingid.SharingIdGenerator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class StandardSender extends Sender {
    public StandardSender(final WebClient slackWebClient,
                          final SlackUrl slackUrl,
                          final SharingIdGenerator sharingIdGenerator) {
        super(slackWebClient.mutate().baseUrl(slackUrl.getUrl()).build(), sharingIdGenerator);
    }

    @Override
    public boolean isSupport(SlackResponse slackResponse) {
        Long mapId = sharingIdGenerator.parseIdFrom(slackResponse.getSharingMapId());
        return !LUTHER_ID.equals(mapId);
    }
}
