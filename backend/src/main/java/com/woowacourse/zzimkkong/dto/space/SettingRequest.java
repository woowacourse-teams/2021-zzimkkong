package com.woowacourse.zzimkkong.dto.space;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.woowacourse.zzimkkong.dto.TimeUnit;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.TIME_FORMAT;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SettingRequest {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT)
    private LocalTime settingStartTime = LocalTime.of(0, 0);

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT)
    private LocalTime settingEndTime = LocalTime.of(23, 59);

    @TimeUnit
    private Integer reservationTimeUnit = 10;

    private Integer reservationMinimumTimeUnit = 10;

    private Integer reservationMaximumTimeUnit = 120;

    private EnabledDayOfWeekDto enabledDayOfWeek = new EnabledDayOfWeekDto();

    public SettingRequest(
            final LocalTime settingStartTime,
            final LocalTime settingEndTime,
            final Integer reservationTimeUnit,
            final Integer reservationMinimumTimeUnit,
            final Integer reservationMaximumTimeUnit,
            final EnabledDayOfWeekDto enabledDayOfWeek) {
        this.settingStartTime = settingStartTime;
        this.settingEndTime = settingEndTime;
        this.reservationTimeUnit = reservationTimeUnit;
        this.reservationMinimumTimeUnit = reservationMinimumTimeUnit;
        this.reservationMaximumTimeUnit = reservationMaximumTimeUnit;
        this.enabledDayOfWeek = enabledDayOfWeek;
    }

    public String enabledDayOfWeekAsString() {
        return enabledDayOfWeek.toString();
    }
}
