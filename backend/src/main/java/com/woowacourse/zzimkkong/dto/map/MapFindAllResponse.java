package com.woowacourse.zzimkkong.dto.map;

import com.woowacourse.zzimkkong.domain.Map;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class MapFindAllResponse {
    private List<MapFindResponse> maps;

    public MapFindAllResponse() {
    }

    public MapFindAllResponse(List<MapFindResponse> maps) {
        this.maps = maps;
    }

    public static MapFindAllResponse from(List<Map> findMaps) {
        return findMaps.stream()
                .map(MapFindResponse::from)
                .collect(collectingAndThen(toList(), MapFindAllResponse::new));
    }

    public List<MapFindResponse> getMaps() {
        return maps;
    }
}
