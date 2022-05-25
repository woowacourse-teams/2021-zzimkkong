package com.woowacourse.zzimkkong.dto.space;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.woowacourse.zzimkkong.domain.Space;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.TIME_FORMAT;

@Getter
@NoArgsConstructor
public class SettingResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT)
    private LocalTime availableStartTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT)
    private LocalTime availableEndTime;
    @JsonProperty
    private Integer reservationTimeUnit;
    @JsonProperty
    private Integer reservationMinimumTimeUnit;
    @JsonProperty
    private Integer reservationMaximumTimeUnit;
    @JsonProperty
    private Boolean reservationEnable;
    @JsonProperty
    private EnabledDayOfWeekDto enabledDayOfWeek;

    protected SettingResponse(
            final LocalTime availableStartTime,
            final LocalTime availableEndTime,
            final Integer reservationTimeUnit,
            final Integer reservationMinimumTimeUnit,
            final Integer reservationMaximumTimeUnit,
            final Boolean reservationEnable,
            final EnabledDayOfWeekDto enabledDayOfWeek) {
        this.availableStartTime = availableStartTime;
        this.availableEndTime = availableEndTime;
        this.reservationTimeUnit = reservationTimeUnit;
        this.reservationMinimumTimeUnit = reservationMinimumTimeUnit;
        this.reservationMaximumTimeUnit = reservationMaximumTimeUnit;
        this.reservationEnable = reservationEnable;
        this.enabledDayOfWeek = enabledDayOfWeek;
    }

    public static SettingResponse from(final Space space) {
        return new SettingResponse(
                space.getAvailableStartTime(),
                space.getAvailableEndTime(),
                space.getReservationTimeUnitAsInt(),
                space.getReservationMinimumTimeUnitAsInt(),
                space.getReservationMaximumTimeUnitAsInt(),
                space.getReservationEnable(),
                EnabledDayOfWeekDto.from(space.getEnabledDayOfWeek())
        );
    }
}
