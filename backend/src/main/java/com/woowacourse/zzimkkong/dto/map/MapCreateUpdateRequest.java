package com.woowacourse.zzimkkong.dto.map;

import javax.validation.constraints.NotBlank;

import static com.woowacourse.zzimkkong.dto.Validator.*;

public class MapCreateUpdateRequest {
    @NotBlank(message = EMPTY_MESSAGE)
    private String mapName;

    @NotBlank(message = EMPTY_MESSAGE)
    private String mapDrawing;

    @NotBlank(message = EMPTY_MESSAGE)
    private String mapSvg;

    public MapCreateUpdateRequest() {
    }

    public MapCreateUpdateRequest(String mapName, String mapDrawing, String mapSvg) {
        this.mapName = mapName;
        this.mapDrawing = mapDrawing;
        this.mapSvg = mapSvg;
    }

    public String getMapName() {
        return mapName;
    }

    public String getMapDrawing() {
        return mapDrawing;
    }

    public String getMapSvg() {
        return mapSvg;
    }
}
