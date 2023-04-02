package com.woowacourse.zzimkkong.dto.space;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SettingsSummaryResponse {
    private String summary;

    private SettingsSummaryResponse(final String summary) {
        this.summary = summary;
    }

    public static SettingsSummaryResponse from(final String summary) {
        return new SettingsSummaryResponse(summary);
    }
}
