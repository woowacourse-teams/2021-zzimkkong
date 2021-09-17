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
    private String managerEmail;

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
                            final String mapImageUrl,
                            final String sharingMapId,
                            final String managerEmail) {
        this.mapId = mapId;
        this.mapName = mapName;
        this.mapDrawing = mapDrawing;
        this.mapImageUrl = mapImageUrl;
        this.sharingMapId = sharingMapId;
        this.managerEmail = managerEmail;
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

    public static MapFindResponse ofAdmin(final Map map,
                                          final String sharingMapId) {
        return new MapFindResponse(
                map.getId(),
                map.getName(),
                map.getMapDrawing(),
                map.getMapImageUrl(),
                sharingMapId,
                map.getMember().getEmail()
        );
    }
}
