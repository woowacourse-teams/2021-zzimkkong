package com.woowacourse.zzimkkong.dto.space;

import javax.validation.constraints.NotBlank;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.EMPTY_MESSAGE;

public class SpaceDeleteRequest {
    @NotBlank(message = EMPTY_MESSAGE)
    private String mapImageSvg;

    public SpaceDeleteRequest() {
    }

    public SpaceDeleteRequest(final String mapImageSvg) {
        this.mapImageSvg = mapImageSvg;
    }

    public String getMapImageSvg() {
        return mapImageSvg;
    }
}
