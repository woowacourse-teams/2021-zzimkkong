package com.woowacourse.zzimkkong.dto.space;

import com.woowacourse.zzimkkong.domain.Space;

public class SpaceFindDetailResponse {
    private Long id;
    private String name;
    private String description;
    private String area;
    private SettingResponse settings;

    public SpaceFindDetailResponse() {
    }

    private SpaceFindDetailResponse(
            final Long id,
            final String name,
            final String description,
            final String area,
            final SettingResponse settings) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.area = area;
        this.settings = settings;
    }

    public static SpaceFindDetailResponse from(final Space space) {
        SettingResponse settingResponse = SettingResponse.from(space);

        return new SpaceFindDetailResponse(
                space.getId(),
                space.getName(),
                space.getDescription(),
                space.getArea(),
                settingResponse);
    }

    public Long getId() {
        return id;
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
