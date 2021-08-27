package com.woowacourse.zzimkkong.dto.map;

import com.woowacourse.zzimkkong.domain.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MapCreateResponse {
    private Long id;

    private MapCreateResponse(final Long id) {
        this.id = id;
    }

    public static MapCreateResponse from(final Map saveMap) {
        return new MapCreateResponse(saveMap.getId());
    }
}
