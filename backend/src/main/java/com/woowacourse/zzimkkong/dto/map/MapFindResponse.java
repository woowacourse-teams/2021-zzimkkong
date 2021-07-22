package com.woowacourse.zzimkkong.dto.map;

import com.woowacourse.zzimkkong.domain.Map;

public class MapFindResponse {
    private Long mapId;
    private String mapName;
    private String mapDrawing;
    private String mapImage;

    public MapFindResponse() {

    }

    private MapFindResponse(Long mapId, String mapName, String mapDrawing, String mapImage) {
        this.mapId = mapId;
        this.mapName = mapName;
        this.mapDrawing = mapDrawing;
        this.mapImage = mapImage;
    }

    public static MapFindResponse from(Map map) {
        return new MapFindResponse(
                map.getId(),
                map.getName(),
                map.getMapDrawing(),
                map.getMapImage()
        );
    }

    public Long getMapId() {
        return mapId;
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
