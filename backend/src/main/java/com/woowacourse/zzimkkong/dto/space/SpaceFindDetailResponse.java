package com.woowacourse.zzimkkong.dto.space;

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
    private String description;
    private String area;
    private Boolean reservationEnable;
    private List<SettingResponse> settings;

    protected SpaceFindDetailResponse(
            final String name,
            final String color,
            final String description,
            final String area,
            final Boolean reservationEnable,
            final List<SettingResponse> settings) {
        this.name = name;
        this.color = color;
        this.description = description;
        this.area = area;
        this.reservationEnable = reservationEnable;
        this.settings = settings;
    }

    public static SpaceFindDetailResponse from(final Space space) {
        List<SettingResponse> settingResponses = getSettingResponses(space);

        return new SpaceFindDetailResponse(
                space.getName(),
                space.getColor(),
                space.getDescription(),
                space.getArea(),
                space.getReservationEnable(),
                settingResponses);
    }

    protected static List<SettingResponse> getSettingResponses(final Space space) {
        return space.getSpaceSettings().getSettings()
                .stream()
                .map(SettingResponse::from)
                .collect(Collectors.toList());
    }
}
