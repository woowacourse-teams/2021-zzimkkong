package com.woowacourse.zzimkkong.dto.admin;

import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.space.SpaceFindDetailWithIdResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
public class SpacesResponse {
    private List<SpaceFindDetailWithIdResponse> spaces;
    private PageInfo pageInfo;

    private SpacesResponse(List<SpaceFindDetailWithIdResponse> spaces, PageInfo pageInfo) {
        this.spaces = spaces;
        this.pageInfo = pageInfo;
    }

    public static SpacesResponse of(List<SpaceFindDetailWithIdResponse> spaces, PageInfo pageInfo) {
        return new SpacesResponse(spaces, pageInfo);
    }

    public static SpacesResponse from(Page<Space> allSpaces) {
        List<SpaceFindDetailWithIdResponse> responses = allSpaces.map(SpaceFindDetailWithIdResponse::fromAdmin).getContent();
        return of(responses, PageInfo.from(allSpaces));
    }
}

