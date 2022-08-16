package com.woowacourse.zzimkkong.dto.space;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Arrays;
import java.util.List;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.EMPTY_MESSAGE;

@Getter
@NoArgsConstructor
public class SpaceCreateUpdateRequest {
    @NotBlank(message = EMPTY_MESSAGE)
    private String name;

    @NotBlank(message = EMPTY_MESSAGE)
    private String color;

    @NotBlank(message = EMPTY_MESSAGE)
    private String description;

    @NotBlank(message = EMPTY_MESSAGE)
    private String area;

    @NotBlank(message = EMPTY_MESSAGE)
    private String thumbnail;

    private Boolean reservationEnable = true;

    @Valid
    private List<SettingRequest> settings = Arrays.asList(new SettingRequest());

    public SpaceCreateUpdateRequest(
            final String name,
            final String color,
            final String description,
            final String area,
            final String thumbnail,
            final Boolean reservationEnable,
            final List<SettingRequest> settings) {
        this.name = name;
        this.color = color;
        this.description = description;
        this.area = area;
        this.thumbnail = thumbnail;
        this.reservationEnable = reservationEnable;
        this.settings = settings;
    }
}
