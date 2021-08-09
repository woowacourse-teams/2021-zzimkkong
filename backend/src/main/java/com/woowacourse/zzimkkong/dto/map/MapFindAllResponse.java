package com.woowacourse.zzimkkong.dto.map;

import com.woowacourse.zzimkkong.domain.Member;

import java.util.List;

public class MapFindAllResponse {
    private List<MapFindResponse> maps;
    private String organization;

    public MapFindAllResponse() {
    }

    private MapFindAllResponse(final List<MapFindResponse> maps, final String organization) {
        this.maps = maps;
        this.organization = organization;
    }

    public static MapFindAllResponse of(final List<MapFindResponse> mapFindResponses, final Member manager) {
        return new MapFindAllResponse(mapFindResponses, manager.getOrganization());
    }

    public List<MapFindResponse> getMaps() {
        return maps;
    }

    public String getOrganization() {
        return organization;
    }
}
