package com.woowacourse.zzimkkong.dto.space;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.woowacourse.zzimkkong.domain.Space;

public class SpaceFindDetailResponse {
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private String area;
    @JsonProperty
    private SettingResponse settings;

    public SpaceFindDetailResponse() {
    }

    protected SpaceFindDetailResponse(
            final String name,
            final String description,
            final String area,
            final SettingResponse settings) {
        this.name = name;
        this.description = description;
        this.area = area;
        this.settings = settings;
    }

    public static SpaceFindDetailResponse from(final Space space) {
        SettingResponse settingResponse = SettingResponse.from(space);

        return new SpaceFindDetailResponse(
                space.getName(),
                space.getDescription(),
                space.getArea(),
                settingResponse);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getArea() {
        return area;
    }

    public SettingResponse getSettings() {
        return settings;
    }
}
