package com.woowacourse.zzimkkong.dto.map;

import javax.validation.constraints.NotBlank;

import static com.woowacourse.zzimkkong.dto.Validator.EMPTY_MESSAGE;

public class MapCreateUpdateRequest {
    @NotBlank(message = EMPTY_MESSAGE)
    private String mapName;

    @NotBlank(message = EMPTY_MESSAGE)
    private String mapDrawing;

    @NotBlank(message = EMPTY_MESSAGE)
    private String mapImageSvg;

    public MapCreateUpdateRequest() {
    }

    public MapCreateUpdateRequest(String mapName, String mapDrawing, String mapImageSvg) {
        this.mapName = mapName;
        this.mapDrawing = mapDrawing;
        this.mapImageSvg = mapImageSvg;
    }

    public String getMapName() {
        return mapName;
    }

    public String getMapDrawing() {
        return mapDrawing;
    }

    public String getMapImageSvg() {
        return mapImageSvg;
    }
}
