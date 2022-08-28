package com.woowacourse.zzimkkong.dto.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.woowacourse.zzimkkong.domain.Preset;
import com.woowacourse.zzimkkong.domain.Setting;
import com.woowacourse.zzimkkong.dto.space.EnabledDayOfWeekDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.TIME_FORMAT;

@Getter
@NoArgsConstructor
public class PresetFindResponse {
    private Long id;
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT)
    private LocalTime settingStartTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT)
    private LocalTime settingEndTime;

    @JsonProperty
    private Integer reservationTimeUnit;

    @JsonProperty
    private Integer reservationMinimumTimeUnit;

    @JsonProperty
    private Integer reservationMaximumTimeUnit;

    @JsonProperty
    private EnabledDayOfWeekDto enabledDayOfWeek;

    public PresetFindResponse(
            final Long id,
            final String name,
            final LocalTime settingStartTime,
            final LocalTime settingEndTime,
            final Integer reservationTimeUnit,
            final Integer reservationMinimumTimeUnit,
            final Integer reservationMaximumTimeUnit,
            final EnabledDayOfWeekDto enabledDayOfWeek) {
        this.id = id;
        this.name = name;
        this.settingStartTime = settingStartTime;
        this.settingEndTime = settingEndTime;
        this.reservationTimeUnit = reservationTimeUnit;
        this.reservationMinimumTimeUnit = reservationMinimumTimeUnit;
        this.reservationMaximumTimeUnit = reservationMaximumTimeUnit;
        this.enabledDayOfWeek = enabledDayOfWeek;
    }

    public static PresetFindResponse from(final Preset preset) {
        return new PresetFindResponse(
                preset.getId(),
                preset.getName(),
                preset.getSettingStartTime(),
                preset.getSettingEndTime(),
                preset.getReservationTimeUnitAsInt(),
                preset.getReservationMinimumTimeUnitAsInt(),
                preset.getReservationMaximumTimeUnitAsInt(),
                EnabledDayOfWeekDto.from(preset.getEnabledDayOfWeek()));
    }
}
