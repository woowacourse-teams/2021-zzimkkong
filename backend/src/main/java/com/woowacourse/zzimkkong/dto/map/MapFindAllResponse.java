package com.woowacourse.zzimkkong.dto.map;

import com.woowacourse.zzimkkong.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MapFindAllResponse {
    private List<MapFindResponse> maps;
    private String organization;

    private MapFindAllResponse(final List<MapFindResponse> maps, final String organization) {
        this.maps = maps;
        this.organization = organization;
    }

    public static MapFindAllResponse of(final List<MapFindResponse> mapFindResponses, final Member manager) {
        return new MapFindAllResponse(mapFindResponses, manager.getOrganization());
    }
}
