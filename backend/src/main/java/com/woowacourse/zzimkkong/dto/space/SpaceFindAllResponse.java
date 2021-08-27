package com.woowacourse.zzimkkong.dto.space;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.woowacourse.zzimkkong.domain.Space;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class SpaceFindAllResponse {
    @JsonProperty
    private List<SpaceFindDetailWithIdResponse> spaces;

    private SpaceFindAllResponse(final List<SpaceFindDetailWithIdResponse> spaces) {
        this.spaces = spaces;
    }

    public static SpaceFindAllResponse from(final List<Space> spaces) {
        List<SpaceFindDetailWithIdResponse> spaceFindDetailResponses = spaces.stream()
                .map(SpaceFindDetailWithIdResponse::from)
                .collect(Collectors.toList());

        return new SpaceFindAllResponse(spaceFindDetailResponses);
    }
}
