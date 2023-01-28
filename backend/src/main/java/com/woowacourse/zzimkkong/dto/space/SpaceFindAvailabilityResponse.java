package com.woowacourse.zzimkkong.dto.space;

import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class SpaceFindAvailabilityResponse {
    private Long spaceId;
    private Boolean isOccupied;
}
