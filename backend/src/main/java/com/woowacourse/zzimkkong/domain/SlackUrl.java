package com.woowacourse.zzimkkong.domain;

import lombok.Getter;

@Getter
public class SlackUrl {
    private String url;

    public SlackUrl() {
    }

    public SlackUrl(final String url) {
        this.url = url;
    }
}
