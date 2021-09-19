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
public class SettingsRequest {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT)
    private LocalTime availableStartTime = LocalTime.of(0, 0);

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT)
    private LocalTime availableEndTime = LocalTime.of(23, 59);

    @TimeUnit
    private Integer reservationTimeUnit = 10;

    private Integer reservationMinimumTimeUnit = 10;

    private Integer reservationMaximumTimeUnit = 120;

    private Boolean reservationEnable = true;

    private EnabledDayOfWeekDto enabledDayOfWeek = new EnabledDayOfWeekDto();

    public SettingsRequest(
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

    public String enabledDayOfWeekAsString() {
        return enabledDayOfWeek.toString();
    }
}
