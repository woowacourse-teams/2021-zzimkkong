package com.woowacourse.zzimkkong.dto.map;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SlackCreateRequest {
    private String slackUrl;

    public SlackCreateRequest(final String slackUrl) {
        this.slackUrl = slackUrl;
    }
}
