package com.woowacourse.zzimkkong.dto.space;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.woowacourse.zzimkkong.domain.Space;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SpaceFindDetailWithIdResponse extends SpaceFindDetailResponse {
    @JsonProperty
    private Long id;

    private SpaceFindDetailWithIdResponse(
            final String name,
            final String color,
            final String description,
            final String area,
            final SettingResponse settings,
            final Long id) {
        super(name, color, description, area, settings);
        this.id = id;
    }

    public static SpaceFindDetailWithIdResponse from(final Space space) {
        SettingResponse settingResponse = SettingResponse.from(space);

        return new SpaceFindDetailWithIdResponse(
                space.getName(),
                space.getColor(),
                space.getDescription(),
                space.getArea(),
                settingResponse,
                space.getId());
    }
}
