package com.woowacourse.zzimkkong.dto.space;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.EMPTY_MESSAGE;

@Getter
@NoArgsConstructor
public class SpaceDeleteRequest {
    @NotBlank(message = EMPTY_MESSAGE)
    private String thumbnail;

    public SpaceDeleteRequest(final String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
