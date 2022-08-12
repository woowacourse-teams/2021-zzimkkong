package com.woowacourse.zzimkkong.domain;

public enum ServiceZone {
    KOREA("Asia/Seoul");

    private final String timeZone;

    ServiceZone(final String timeZone) {
        this.timeZone = timeZone;
    }

    public String getTimeZone() {
        return timeZone;
    }
}
