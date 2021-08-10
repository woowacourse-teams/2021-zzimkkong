package com.woowacourse.zzimkkong.dto.member;

import com.woowacourse.zzimkkong.domain.Preset;
import com.woowacourse.zzimkkong.domain.Setting;
import com.woowacourse.zzimkkong.dto.space.SettingResponse;

import java.time.LocalTime;

public class PresetFindResponse extends SettingResponse {
    private String name;

    public PresetFindResponse() {
    }

    private PresetFindResponse(
            final LocalTime availableStartTime,
            final LocalTime availableEndTime,
            final Integer reservationTimeUnit,
            final Integer reservationMinimumTimeUnit,
            final Integer reservationMaximumTimeUnit,
            final Boolean reservationEnable,
            final String enabledDayOfWeek,
            final String name) {
        super(availableStartTime, availableEndTime, reservationTimeUnit, reservationMinimumTimeUnit, reservationMaximumTimeUnit, reservationEnable, enabledDayOfWeek);
        this.name = name;
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
                setting.getEnabledDayOfWeek(),
                preset.getName());
    }

    public String getName() {
        return name;
    }
}
