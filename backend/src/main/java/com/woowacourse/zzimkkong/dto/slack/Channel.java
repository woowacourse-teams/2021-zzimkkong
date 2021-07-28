package com.woowacourse.zzimkkong.dto.slack;

public enum Channel {
    TEST("T027MKKBGMN/B0284P", "TF88J/O3iGWpdntNXKrfJy4CkjyHeb", "알림테스트"),
    LOCAL("T027MKKBGMN/B028UE", "Q99L0/cLSODp2veIJ1iGQjcva2Q8j9", "zzimkkong-reservation");

    private final String urlStart;
    private final String urlEnd;
    private final String name;

    Channel(final String urlStart, final String urlEnd, final String name) {
        this.urlStart = urlStart;
        this.urlEnd = urlEnd;
        this.name = name;
    }

    public String url() {
        return urlStart + urlEnd;
    }
}
