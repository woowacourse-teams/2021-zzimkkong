package com.woowacourse.zzimkkong.dto.map;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.EMPTY_MESSAGE;

@Getter
@NoArgsConstructor
public class MapCreateUpdateRequest {
    @NotBlank(message = EMPTY_MESSAGE)
    private String mapName;

    @NotBlank(message = EMPTY_MESSAGE)
    private String mapDrawing;

    @NotBlank(message = EMPTY_MESSAGE)
    private String mapImageSvg;

    public MapCreateUpdateRequest(final String mapName, final String mapDrawing, final String mapImageSvg) {
        this.mapName = mapName;
        this.mapDrawing = mapDrawing;
        this.mapImageSvg = mapImageSvg;
    }
}
