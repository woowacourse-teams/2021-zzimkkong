package com.woowacourse.zzimkkong.dto.space;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.woowacourse.zzimkkong.domain.Space;

public class SpaceFindDetailWithIdResponse extends SpaceFindDetailResponse {
    @JsonProperty
    private Long id;

    public SpaceFindDetailWithIdResponse() {
    }

    private SpaceFindDetailWithIdResponse(
            final String name,
            final String description,
            final String area,
            final SettingResponse settings,
            final Long id) {
        super(name, description, area, settings);
        this.id = id;
    }

    public static SpaceFindDetailWithIdResponse from(final Space space) {
        SettingResponse settingResponse = SettingResponse.from(space);

        return new SpaceFindDetailWithIdResponse(
                space.getName(),
                space.getDescription(),
                space.getArea(),
                settingResponse,
                space.getId());
    }

    public Long getId() {
        return id;
    }
}
