package com.woowacourse.zzimkkong.dto.space;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.woowacourse.zzimkkong.domain.Setting;
import com.woowacourse.zzimkkong.domain.Space;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.TIME_FORMAT;

@Getter
@NoArgsConstructor
public class SettingResponse {
    @JsonProperty
    private Long settingId;

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

    protected SettingResponse(
            final Long settingId,
            final LocalTime settingStartTime,
            final LocalTime settingEndTime,
            final Integer reservationTimeUnit,
            final Integer reservationMinimumTimeUnit,
            final Integer reservationMaximumTimeUnit,
            final EnabledDayOfWeekDto enabledDayOfWeek) {
        this.settingId = settingId;
        this.settingStartTime = settingStartTime;
        this.settingEndTime = settingEndTime;
        this.reservationTimeUnit = reservationTimeUnit;
        this.reservationMinimumTimeUnit = reservationMinimumTimeUnit;
        this.reservationMaximumTimeUnit = reservationMaximumTimeUnit;
        this.enabledDayOfWeek = enabledDayOfWeek;
    }

    public static SettingResponse from(final Setting setting) {
        return new SettingResponse(
                setting.getId(),
                setting.getSettingStartTime(),
                setting.getSettingEndTime(),
                setting.getReservationTimeUnitAsInt(),
                setting.getReservationMinimumTimeUnitAsInt(),
                setting.getReservationMaximumTimeUnitAsInt(),
                EnabledDayOfWeekDto.from(setting.getEnabledDayOfWeek())
        );
    }
}
