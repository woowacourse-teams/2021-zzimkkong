package com.woowacourse.zzimkkong.dto.space;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.woowacourse.zzimkkong.domain.Space;

import java.util.List;
import java.util.stream.Collectors;

public class SpaceFindAllResponse {
    @JsonProperty
    private List<SpaceFindDetailResponse> spaces;

    public SpaceFindAllResponse() {
    }

    private SpaceFindAllResponse(final List<SpaceFindDetailResponse> spaces) {
        this.spaces = spaces;
    }

    public static SpaceFindAllResponse from(final List<Space> spaces) {
        List<SpaceFindDetailResponse> spaceFindDetailResponses = spaces.stream()
                .map(SpaceFindDetailResponse::from)
                .collect(Collectors.toList());

        return new SpaceFindAllResponse(spaceFindDetailResponses);
    }
}
