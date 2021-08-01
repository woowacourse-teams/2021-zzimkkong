package com.woowacourse.zzimkkong.dto.map;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class MapFindAllResponse {
    private List<MapFindResponse> maps;
    private String organization;

    public MapFindAllResponse() {
    }

    private MapFindAllResponse(final List<MapFindResponse> maps, final String organization) {
        this.maps = maps;
        this.organization = organization;
    }

    public static MapFindAllResponse of(final List<Map> findMaps, final Member manager) {
        return findMaps.stream()
                .map(MapFindResponse::from)
                .collect(Collectors.collectingAndThen(toList(), list -> new MapFindAllResponse(list, manager.getOrganization())));
    }

    public List<MapFindResponse> getMaps() {
        return maps;
    }

    public String getOrganization() {
        return organization;
    }
}
