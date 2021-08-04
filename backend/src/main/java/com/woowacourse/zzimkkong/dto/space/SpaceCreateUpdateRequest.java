package com.woowacourse.zzimkkong.dto.space;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.EMPTY_MESSAGE;

public class SpaceCreateUpdateRequest {
    @NotBlank(message = EMPTY_MESSAGE)
    private String spaceName;

    @NotBlank(message = EMPTY_MESSAGE)
    private String color;

    @NotBlank(message = EMPTY_MESSAGE)
    private String description;

    @NotBlank(message = EMPTY_MESSAGE)
    private String area;

    @Valid
    private SettingsRequest settingsRequest;

    public SpaceCreateUpdateRequest() {
    }

    public SpaceCreateUpdateRequest(
            final String spaceName,
            final String color,
            final String description,
            final String area,
            final SettingsRequest settingsRequest) {
        this.spaceName = spaceName;
        this.color = color;
        this.description = description;
        this.area = area;
        this.settingsRequest = settingsRequest;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public String getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }

    public String getArea() {
        return area;
    }

    public SettingsRequest getSettingsRequest() {
        return settingsRequest;
    }
}
