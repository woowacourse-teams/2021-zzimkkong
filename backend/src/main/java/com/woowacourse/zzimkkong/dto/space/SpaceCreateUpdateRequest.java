package com.woowacourse.zzimkkong.dto.space;

import javax.validation.constraints.NotBlank;

import static com.woowacourse.zzimkkong.dto.Validator.EMPTY_MESSAGE;

public class SpaceCreateUpdateRequest {
    @NotBlank(message = EMPTY_MESSAGE)
    private String spaceName;
    @NotBlank(message = EMPTY_MESSAGE)
    private String description;
    @NotBlank(message = EMPTY_MESSAGE)
    private String area;
    private SettingsRequest settingsRequest;
    @NotBlank(message = EMPTY_MESSAGE)
    private String mapImage;    // todo mapImageSvg로 칼럼명 통일

    public SpaceCreateUpdateRequest() {
    }

    public SpaceCreateUpdateRequest(
            final String spaceName,
            final String description,
            final String area,
            final SettingsRequest settingsRequest,
            final String mapImage) {
        this.spaceName = spaceName;
        this.description = description;
        this.area = area;
        this.settingsRequest = settingsRequest;
        this.mapImage = mapImage;
    }

    public String getSpaceName() {
        return spaceName;
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

    public String getMapImage() {
        return mapImage;
    }
}
