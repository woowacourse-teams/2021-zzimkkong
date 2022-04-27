package com.woowacourse.zzimkkong.dto.map;

import com.woowacourse.zzimkkong.domain.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SlackFindResponse {
    private String slackUrl;

    private SlackFindResponse(final String slackUrl) {
        this.slackUrl = slackUrl;
    }

    public static SlackFindResponse from(Map map) {
        return new SlackFindResponse(map.getSlackUrl());
    }
}
