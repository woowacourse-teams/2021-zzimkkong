package com.woowacourse.zzimkkong.dto.space;

public class SpaceCreateRequest {
    private String spaceName;
    private String description;
    private String area;
    private SettingsRequest settingsRequest;
    private String mapImage;

    public SpaceCreateRequest() {
    }

    public SpaceCreateRequest(
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
