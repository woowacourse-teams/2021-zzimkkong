package com.woowacourse.zzimkkong.dto.map;

import com.woowacourse.zzimkkong.domain.Map;

public class MapFindResponse {
    private Long mapId;
    private String mapName;
    private String mapDrawing;
    private String mapImageUrl;
    private String sharingMapId;

    public MapFindResponse() {
    }

    private MapFindResponse(final Long mapId,
                            final String mapName,
                            final String mapDrawing,
                            final String mapImageUrl,
                            final String sharingMapId) {
        this.mapId = mapId;
        this.mapName = mapName;
        this.mapDrawing = mapDrawing;
        this.mapImageUrl = mapImageUrl;
        this.sharingMapId = sharingMapId;
    }

    public static MapFindResponse of(final Map map,
                                     final String sharingMapId) {
        return new MapFindResponse(
                map.getId(),
                map.getName(),
                map.getMapDrawing(),
                map.getMapImageUrl(),
                sharingMapId
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

    public String getMapImageUrl() {
        return mapImageUrl;
    }

    public String getSharingMapId() {
        return sharingMapId;
    }
}
