package com.woowacourse.zzimkkong.dto.space;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@Getter
public class SpaceFindAllAvailabilityResponse {
    private Long mapId;
    private List<SpaceFindAvailabilityResponse> spaces;

    public static SpaceFindAllAvailabilityResponse of(
            final Long mapId,
            final List<Space> allSpaces,
            final Set<Space> occupiedSpaces) {
        List<SpaceFindAvailabilityResponse> spaces = allSpaces.stream()
                .map(space -> SpaceFindAvailabilityResponse.builder()
                        .spaceId(space.getId())
                        .isOccupied(occupiedSpaces.contains(space))
                        .build())
                .collect(Collectors.toList());

        return SpaceFindAllAvailabilityResponse.builder()
                .mapId(mapId)
                .spaces(spaces)
                .build();
    }
}
