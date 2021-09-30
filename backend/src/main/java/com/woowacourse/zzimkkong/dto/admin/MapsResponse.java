package com.woowacourse.zzimkkong.dto.admin;

import com.woowacourse.zzimkkong.dto.map.MapFindResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MapsResponse {
    private List<MapFindResponse> maps;
    private PageInfo pageInfo;

    private MapsResponse(List<MapFindResponse> maps, PageInfo pageInfo) {
        this.maps = maps;
        this.pageInfo = pageInfo;
    }

    public static MapsResponse from(List<MapFindResponse> maps, PageInfo pageInfo) {
        return new MapsResponse(maps, pageInfo);
    }
}

