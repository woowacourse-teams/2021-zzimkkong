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
    private String thumbnail;
    private String sharingMapId;
    private String slackUrl;
    private String notice;
    private String managerEmail;

    private MapFindResponse(final Long mapId,
                            final String mapName,
                            final String mapDrawing,
                            final String thumbnail,
                            final String slackUrl,
                            final String notice,
                            final String sharingMapId) {
        this.mapId = mapId;
        this.mapName = mapName;
        this.mapDrawing = mapDrawing;
        this.thumbnail = thumbnail;
        this.slackUrl = slackUrl;
        this.notice = notice;
        this.sharingMapId = sharingMapId;
    }

    private MapFindResponse(final Long mapId,
                            final String mapName,
                            final String mapDrawing,
                            final String thumbnail,
                            final String slackUrl,
                            final String notice,
                            final String sharingMapId,
                            final String managerEmail) {
        this.mapId = mapId;
        this.mapName = mapName;
        this.mapDrawing = mapDrawing;
        this.thumbnail = thumbnail;
        this.slackUrl = slackUrl;
        this.notice = notice;
        this.sharingMapId = sharingMapId;
        this.managerEmail = managerEmail;
    }

    public static MapFindResponse of(final Map map) {
        return new MapFindResponse(
                map.getId(),
                map.getName(),
                map.getMapDrawing(),
                map.getThumbnail(),
                map.getSlackUrl(),
                map.getNotice(),
                map.getSharingMapId()
        );
    }

    public static MapFindResponse ofAdmin(final Map map,
                                          final String sharingMapId) {
        return new MapFindResponse(
                map.getId(),
                map.getName(),
                map.getMapDrawing(),
                map.getThumbnail(),
                map.getSlackUrl(),
                map.getNotice(),
                sharingMapId,
                map.getMember().getEmail()
        );
    }
}
