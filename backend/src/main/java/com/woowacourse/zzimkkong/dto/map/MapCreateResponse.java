package com.woowacourse.zzimkkong.dto.map;

import com.woowacourse.zzimkkong.domain.Map;

public class MapCreateResponse {
    private Long id;

    public MapCreateResponse() {
    }

    private MapCreateResponse(final Long id) {
        this.id = id;
    }

    public static MapCreateResponse from(final Map saveMap) {
        return new MapCreateResponse(saveMap.getId());
    }

    public Long getId() {
        return id;
    }
}
