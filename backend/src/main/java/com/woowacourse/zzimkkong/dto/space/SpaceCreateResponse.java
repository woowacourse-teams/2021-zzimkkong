package com.woowacourse.zzimkkong.dto.space;

import com.woowacourse.zzimkkong.domain.Space;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SpaceCreateResponse {
    private Long id;

    private SpaceCreateResponse(final Long id) {
        this.id = id;
    }

    public static SpaceCreateResponse from(final Space space) {
        return new SpaceCreateResponse(space.getId());
    }
}
