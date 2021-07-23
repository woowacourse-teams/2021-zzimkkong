package com.woowacourse.zzimkkong.dto.space;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.woowacourse.zzimkkong.domain.Space;

import java.time.LocalTime;

import static com.woowacourse.zzimkkong.dto.Validator.TIME_FORMAT;

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
    private String disabledWeekdays;

    public SettingResponse() {
    }

    private SettingResponse(
            final LocalTime availableStartTime,
            final LocalTime availableEndTime,
            final Integer reservationTimeUnit,
            final Integer reservationMinimumTimeUnit,
            final Integer reservationMaximumTimeUnit,
            final Boolean reservationEnable,
            final String disabledWeekdays) {
        this.availableStartTime = availableStartTime;
        this.availableEndTime = availableEndTime;
        this.reservationTimeUnit = reservationTimeUnit;
        this.reservationMinimumTimeUnit = reservationMinimumTimeUnit;
        this.reservationMaximumTimeUnit = reservationMaximumTimeUnit;
        this.reservationEnable = reservationEnable;
        this.disabledWeekdays = disabledWeekdays;
    }

    public static SettingResponse from(final Space space) {
        return new SettingResponse(
                space.getAvailableStartTime(),
                space.getAvailableEndTime(),
                space.getReservationTimeUnit(),
                space.getReservationMinimumTimeUnit(),
                space.getReservationMaximumTimeUnit(),
                space.getReservationEnable(),
                space.getDisabledWeekdays()
        );
    }
}
