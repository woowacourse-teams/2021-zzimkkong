package com.woowacourse.zzimkkong.dto.member;

import com.woowacourse.zzimkkong.domain.Preset;
import com.woowacourse.zzimkkong.domain.Setting;
import com.woowacourse.zzimkkong.dto.space.EnabledDayOfWeekResponse;
import com.woowacourse.zzimkkong.dto.space.SettingResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class PresetFindResponse extends SettingResponse {
    private Long id;
    private String name;

    private PresetFindResponse(
            final Long id,
            final LocalTime availableStartTime,
            final LocalTime availableEndTime,
            final Integer reservationTimeUnit,
            final Integer reservationMinimumTimeUnit,
            final Integer reservationMaximumTimeUnit,
            final Boolean reservationEnable,
            final EnabledDayOfWeekResponse enabledDayOfWeek,
            final String name) {
        super(availableStartTime, availableEndTime, reservationTimeUnit, reservationMinimumTimeUnit, reservationMaximumTimeUnit, reservationEnable, enabledDayOfWeek);
        this.id = id;
        this.name = name;
    }

    public static PresetFindResponse from(final Preset preset) {
        Setting setting = preset.getSetting();

        return new PresetFindResponse(
                preset.getId(),
                setting.getAvailableStartTime(),
                setting.getAvailableEndTime(),
                setting.getReservationTimeUnit(),
                setting.getReservationMinimumTimeUnit(),
                setting.getReservationMaximumTimeUnit(),
                setting.getReservationEnable(),
                EnabledDayOfWeekResponse.from(setting.getEnabledDayOfWeek()),
                preset.getName());
    }
}
