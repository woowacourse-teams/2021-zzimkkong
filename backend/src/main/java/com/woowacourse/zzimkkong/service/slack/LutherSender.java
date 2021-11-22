package com.woowacourse.zzimkkong.service.slack;

import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.infrastructure.sharingid.SharingIdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Profile("prod")
public class LutherSender extends Sender {
    public LutherSender(final WebClient slackWebClient,
                        @Value("${slack.webhook.luther}") final String slackUrl,
                        final SharingIdGenerator sharingIdGenerator) {
        super(slackWebClient.mutate().baseUrl(slackUrl).build(), sharingIdGenerator);
    }

    @Override
    public boolean isSupport(SlackResponse slackResponse) {
        Long mapId = sharingIdGenerator.parseIdFrom(slackResponse.getSharingMapId());
        return LUTHER_ID.equals(mapId);
    }
}
