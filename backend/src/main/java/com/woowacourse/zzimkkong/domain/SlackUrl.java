package com.woowacourse.zzimkkong.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SlackUrl {
    private String url;

    public SlackUrl(final String url) {
        this.url = url;
    }
}
