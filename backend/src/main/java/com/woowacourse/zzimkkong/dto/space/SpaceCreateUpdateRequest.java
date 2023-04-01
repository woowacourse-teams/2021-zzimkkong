package com.woowacourse.zzimkkong.dto.space;

import com.woowacourse.zzimkkong.domain.Settings;
import com.woowacourse.zzimkkong.dto.NotDuplicatedSettingOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.List;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.EMPTY_MESSAGE;
import static com.woowacourse.zzimkkong.dto.ValidatorMessage.SETTING_COUNT_MESSAGE;

@Getter
@NoArgsConstructor
public class SpaceCreateUpdateRequest {
    @NotBlank(message = EMPTY_MESSAGE)
    private String name;

    @NotBlank(message = EMPTY_MESSAGE)
    private String color;

    @NotBlank(message = EMPTY_MESSAGE)
    private String area;

    @NotBlank(message = EMPTY_MESSAGE)
    private String thumbnail;

    private Boolean reservationEnable = true;

    @NotNull(message = SETTING_COUNT_MESSAGE)
    @Size(min = Settings.MINIMUM_SETTING_COUNT, message = SETTING_COUNT_MESSAGE)
    @NotDuplicatedSettingOrder
    @Valid
    private List<SettingRequest> settings = Arrays.asList(new SettingRequest());

    public SpaceCreateUpdateRequest(
            final String name,
            final String color,
            final String area,
            final String thumbnail,
            final Boolean reservationEnable,
            final List<SettingRequest> settings) {
        this.name = name;
        this.color = color;
        this.area = area;
        this.thumbnail = thumbnail;
        this.reservationEnable = reservationEnable;
        this.settings = settings;
    }
}
