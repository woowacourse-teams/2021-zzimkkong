package com.woowacourse.zzimkkong.dto.map;

import com.woowacourse.zzimkkong.domain.Map;
import lombok.Getter;

@Getter
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
}
