package com.woowacourse.zzimkkong.dto.member;

import com.woowacourse.zzimkkong.domain.Preset;
import com.woowacourse.zzimkkong.domain.Setting;

import java.time.LocalTime;

public class PresetFindResponse {
    private LocalTime availableStartTime;
    private LocalTime availableEndTime;
    private Integer reservationTimeUnit;
    private Integer reservationMinimumTimeUnit;
    private Integer reservationMaximumTimeUnit;
    private Boolean reservationEnable;
    private String enabledDayOfWeek;

    public PresetFindResponse() {
    }

    private PresetFindResponse(
            final LocalTime availableStartTime,
            final LocalTime availableEndTime,
            final Integer reservationTimeUnit,
            final Integer reservationMinimumTimeUnit,
            final Integer reservationMaximumTimeUnit,
            final Boolean reservationEnable,
            final String enabledDayOfWeek) {
        this.availableStartTime = availableStartTime;
        this.availableEndTime = availableEndTime;
        this.reservationTimeUnit = reservationTimeUnit;
        this.reservationMinimumTimeUnit = reservationMinimumTimeUnit;
        this.reservationMaximumTimeUnit = reservationMaximumTimeUnit;
        this.reservationEnable = reservationEnable;
        this.enabledDayOfWeek = enabledDayOfWeek;
    }

    public static PresetFindResponse from(final Preset preset) {
        Setting setting = preset.getSetting();

        return new PresetFindResponse(
                setting.getAvailableStartTime(),
                setting.getAvailableEndTime(),
                setting.getReservationTimeUnit(),
                setting.getReservationMinimumTimeUnit(),
                setting.getReservationMaximumTimeUnit(),
                setting.getReservationEnable(),
                setting.getEnabledDayOfWeek());
    }
}
