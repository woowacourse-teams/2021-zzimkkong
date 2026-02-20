package com.woowacourse.zzimkkong.dto.space;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Group;
import com.woowacourse.zzimkkong.domain.Space;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

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
            final String area,
            final Boolean reservationEnable,
            final List<SettingResponse> settings,
            final List<Group> allowedGroups,
            final Long id) {
        super(name, color, area, reservationEnable, settings, allowedGroups);
        this.id = id;
    }

    private SpaceFindDetailWithIdResponse(
            final String name,
            final String color,
            final String area,
            final Boolean reservationEnable,
            final List<SettingResponse> settings,
            final List<Group> allowedGroups,
            final Long id,
            final Long managerId,
            final Long mapId) {
        super(name, color, area, reservationEnable, settings, allowedGroups);
        this.id = id;
        this.managerId = managerId;
        this.mapId = mapId;
    }

    public static SpaceFindDetailWithIdResponse from(final Space space) {
        List<SettingResponse> settingResponses = getSettingResponses(space);
        List<Group> allowedGroups = space.getAllowedGroups()
                .stream()
                .map(allowedGroup -> allowedGroup.getGroup())
                .collect(Collectors.toList());

        return new SpaceFindDetailWithIdResponse(
                space.getName(),
                space.getColor(),
                space.getArea(),
                space.getReservationEnable(),
                settingResponses,
                allowedGroups,
                space.getId());
    }

    public static SpaceFindDetailWithIdResponse fromAdmin(final Space space) {
        List<SettingResponse> settingResponses = getSettingResponses(space);
        List<Group> allowedGroups = space.getAllowedGroups()
                .stream()
                .map(allowedGroup -> allowedGroup.getGroup())
                .collect(Collectors.toList());

        Map map = space.getMap();
        Member member = map.getMember();
        return new SpaceFindDetailWithIdResponse(
                space.getName(),
                space.getColor(),
                space.getArea(),
                space.getReservationEnable(),
                settingResponses,
                allowedGroups,
                space.getId(),
                member.getId(),
                map.getId());
    }
}
