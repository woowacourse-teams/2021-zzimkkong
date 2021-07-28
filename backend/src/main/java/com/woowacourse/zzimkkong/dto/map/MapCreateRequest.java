package com.woowacourse.zzimkkong.dto.map;

import javax.validation.constraints.NotBlank;

import static com.woowacourse.zzimkkong.dto.Validator.EMPTY_MESSAGE;

public class MapCreateRequest {
    @NotBlank(message = EMPTY_MESSAGE)
    private String mapName;

    @NotBlank(message = EMPTY_MESSAGE)
    private String mapDrawing;

    @NotBlank(message = EMPTY_MESSAGE)
    private String mapImage;

    public MapCreateRequest() {
    }

    public MapCreateRequest(String mapName, String mapDrawing, String mapImage) {
        this.mapName = mapName;
        this.mapDrawing = mapDrawing;
        this.mapImage = mapImage;
    }

    public String getMapName() {
        return mapName;
    }

    public String getMapDrawing() {
        return mapDrawing;
    }

    public String getMapImage() {
        return mapImage;
    }
}
