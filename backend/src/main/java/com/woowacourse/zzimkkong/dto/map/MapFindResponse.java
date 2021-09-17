package com.woowacourse.zzimkkong.dto.map;

import com.woowacourse.zzimkkong.domain.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MapFindResponse {
    private Long mapId;
    private String mapName;
    private String mapDrawing;
    private String mapImageUrl;
    private String sharingMapId;

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

    private MapFindResponse(final Long mapId,
                            final String mapName,
                            final String mapDrawing,
                            final String mapImageUrl) {
        this.mapId = mapId;
        this.mapName = mapName;
        this.mapDrawing = mapDrawing;
        this.mapImageUrl = mapImageUrl;
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

    public static MapFindResponse from(final Map map) {
        return new MapFindResponse(
                map.getId(),
                map.getName(),
                map.getMapDrawing(),
                map.getMapImageUrl()
        );
    }
}
