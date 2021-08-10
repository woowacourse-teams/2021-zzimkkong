package com.woowacourse.zzimkkong.dto.space;

import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.EMPTY_MESSAGE;

@Getter
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
    private String mapImageSvg;

    @Valid
    private SettingsRequest settingsRequest;

    public SpaceCreateUpdateRequest() {
    }

    public SpaceCreateUpdateRequest(
            final String name,
            final String color,
            final String description,
            final String area,
            final SettingsRequest settingsRequest,
            final String mapImageSvg) {
        this.name = name;
        this.color = color;
        this.description = description;
        this.area = area;
        this.settingsRequest = settingsRequest;
        this.mapImageSvg = mapImageSvg;
    }
}
