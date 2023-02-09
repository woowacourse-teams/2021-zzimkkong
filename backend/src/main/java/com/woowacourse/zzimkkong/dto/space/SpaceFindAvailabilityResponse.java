package com.woowacourse.zzimkkong.dto.space;

import com.woowacourse.zzimkkong.domain.Space;
import lombok.Builder;
import lombok.Getter;

import java.util.Collection;
import java.util.Set;


@Builder
@Getter
public class SpaceFindAvailabilityResponse {
    private Long spaceId;
    private Boolean isAvailable;

    public static SpaceFindAvailabilityResponse of(final Space space, final Collection<Space> occupiedSpaces) {
        return SpaceFindAvailabilityResponse.builder()
                .spaceId(space.getId())
                .isAvailable(!occupiedSpaces.contains(space))
                .build();
    }
}
