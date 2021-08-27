package com.woowacourse.zzimkkong.dto.space;

import com.woowacourse.zzimkkong.domain.Space;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SpaceFindDetailResponse {
    private String name;
    private String color;
    private String description;
    private String area;
    private SettingResponse settings;

    protected SpaceFindDetailResponse(
            final String name,
            final String color,
            final String description,
            final String area,
            final SettingResponse settings) {
        this.name = name;
        this.color = color;
        this.description = description;
        this.area = area;
        this.settings = settings;
    }

    public static SpaceFindDetailResponse from(final Space space) {
        SettingResponse settingResponse = SettingResponse.from(space);

        return new SpaceFindDetailResponse(
                space.getName(),
                space.getColor(),
                space.getDescription(),
                space.getArea(),
                settingResponse);
    }
}
