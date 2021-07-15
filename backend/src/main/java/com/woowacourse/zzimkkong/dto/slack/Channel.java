package com.woowacourse.zzimkkong.dto.slack;

public enum Channel {
    TEST("https://hooks.slack.com/services/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", "알림테스트"),
    LOCAL("https://hooks.slack.com/services/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", "zzimkkong-reservation");

    private String url;
    private String name;

    Channel(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public String getUrl() {
        return url;
    }
}
