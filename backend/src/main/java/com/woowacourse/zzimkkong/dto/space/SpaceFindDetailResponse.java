package com.woowacourse.zzimkkong.dto.space;

import com.woowacourse.zzimkkong.domain.Group;
import com.woowacourse.zzimkkong.domain.Space;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class SpaceFindDetailResponse {
    private String name;
    private String color;
    private String area;
    private Boolean reservationEnable;
    private List<SettingResponse> settings;
    private List<Group> allowedGroups;

    protected SpaceFindDetailResponse(
            final String name,
            final String color,
            final String area,
            final Boolean reservationEnable,
            final List<SettingResponse> settings,
            final List<Group> allowedGroups) {
        this.name = name;
        this.color = color;
        this.area = area;
        this.reservationEnable = reservationEnable;
        this.settings = settings;
        this.allowedGroups = allowedGroups;
    }

    public static SpaceFindDetailResponse from(final Space space) {
        List<SettingResponse> settingResponses = getSettingResponses(space);
        List<Group> allowedGroups = space.getAllowedGroups()
                .stream()
                .map(allowedGroup -> allowedGroup.getGroup())
                .collect(Collectors.toList());

        return new SpaceFindDetailResponse(
                space.getName(),
                space.getColor(),
                space.getArea(),
                space.getReservationEnable(),
                settingResponses,
                allowedGroups);
    }

    protected static List<SettingResponse> getSettingResponses(final Space space) {
        return space.getSpaceSettings().getSettings()
                .stream()
                .map(SettingResponse::from)
                .collect(Collectors.toList());
    }
}
