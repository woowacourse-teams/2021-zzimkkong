package com.woowacourse.zzimkkong.dto.member;

import com.woowacourse.zzimkkong.dto.space.SettingRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.*;

@Getter
@NoArgsConstructor
public class PresetCreateRequest {
    @NotNull(message = EMPTY_MESSAGE)
    @Pattern(regexp = PRESET_NAME_FORMAT, message = PRESET_NAME_MESSAGE)
    private String name;

    @Valid
    private SettingRequest preset;

    public PresetCreateRequest(final String name, final SettingRequest preset) {
        this.name = name;
        this.preset = preset;
    }
}
