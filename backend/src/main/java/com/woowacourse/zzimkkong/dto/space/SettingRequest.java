package com.woowacourse.zzimkkong.dto.space;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.woowacourse.zzimkkong.dto.TimeUnit;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.*;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SettingRequest {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT)
    private LocalTime settingStartTime = LocalTime.of(7, 0);

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT)
    private LocalTime settingEndTime = LocalTime.of(23, 0);

    @TimeUnit
    private Integer reservationTimeUnit = 10;

    private Integer reservationMinimumTimeUnit = 10;

    private Integer reservationMaximumTimeUnit = 120;

    private EnabledDayOfWeekDto enabledDayOfWeek = new EnabledDayOfWeekDto();

    @NotNull(message = EMPTY_MESSAGE)
    @Min(value = 0, message = INVALID_SETTING_ORDER_MESSAGE)
    private Integer order = 0;

    public SettingRequest(
            final LocalTime settingStartTime,
            final LocalTime settingEndTime,
            final Integer reservationTimeUnit,
            final Integer reservationMinimumTimeUnit,
            final Integer reservationMaximumTimeUnit,
            final EnabledDayOfWeekDto enabledDayOfWeek,
            final Integer order) {
        this.settingStartTime = settingStartTime;
        this.settingEndTime = settingEndTime;
        this.reservationTimeUnit = reservationTimeUnit;
        this.reservationMinimumTimeUnit = reservationMinimumTimeUnit;
        this.reservationMaximumTimeUnit = reservationMaximumTimeUnit;
        this.enabledDayOfWeek = enabledDayOfWeek;
        this.order = order;
    }

    public String enabledDayOfWeekAsString() {
        return enabledDayOfWeek.toString();
    }
}
