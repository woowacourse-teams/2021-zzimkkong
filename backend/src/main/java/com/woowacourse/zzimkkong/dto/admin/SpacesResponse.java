package com.woowacourse.zzimkkong.dto.admin;

import com.woowacourse.zzimkkong.dto.space.SpaceFindDetailWithIdResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SpacesResponse {
    private List<SpaceFindDetailWithIdResponse> spaces;
    private PageInfo pageInfo;

    public SpacesResponse(List<SpaceFindDetailWithIdResponse> spaces, PageInfo pageInfo) {
        this.spaces = spaces;
        this.pageInfo = pageInfo;
    }

    public static SpacesResponse from(List<SpaceFindDetailWithIdResponse> maps, PageInfo pageInfo) {
        return new SpacesResponse(maps, pageInfo);
    }
}

