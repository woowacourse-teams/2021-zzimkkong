package com.woowacourse.zzimkkong.dto.space;

public class SpaceCreateUpdateRequest {
    private String name;
    private String description;
    private String area;
    private SettingsRequest settingsRequest;
    private String mapImage;

    public SpaceCreateUpdateRequest() {
    }

    public SpaceCreateUpdateRequest(
            final String name,
            final String description,
            final String area,
            final SettingsRequest settingsRequest,
            final String mapImage) {
        this.name = name;
        this.description = description;
        this.area = area;
        this.settingsRequest = settingsRequest;
        this.mapImage = mapImage;
    }

    public String getName() {
        return name;
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
