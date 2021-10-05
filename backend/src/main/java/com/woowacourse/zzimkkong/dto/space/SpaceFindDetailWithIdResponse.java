package com.woowacourse.zzimkkong.dto.space;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Space;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SpaceFindDetailWithIdResponse extends SpaceFindDetailResponse {
    @JsonProperty
    private Long id;
    private Long managerId;
    private Long mapId;

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

    private SpaceFindDetailWithIdResponse(
            final String name,
            final String color,
            final String description,
            final String area,
            final SettingResponse settings,
            final Long id,
            final Long managerId,
            final Long mapId) {
        super(name, color, description, area, settings);
        this.id = id;
        this.managerId = managerId;
        this.mapId = mapId;
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

    public static SpaceFindDetailWithIdResponse fromAdmin(final Space space) {
        SettingResponse settingResponse = SettingResponse.from(space);

        Map map = space.getMap();
        Member member = map.getMember();
        return new SpaceFindDetailWithIdResponse(
                space.getName(),
                space.getColor(),
                space.getDescription(),
                space.getArea(),
                settingResponse,
                space.getId(),
                member.getId(),
                map.getId());
    }
}
