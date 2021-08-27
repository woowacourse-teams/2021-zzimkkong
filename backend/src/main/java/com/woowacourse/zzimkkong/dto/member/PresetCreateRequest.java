package com.woowacourse.zzimkkong.dto.member;

import com.woowacourse.zzimkkong.dto.space.SettingsRequest;
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
    @Pattern(regexp = NAMING_FORMAT, message = NAME_MESSAGE)
    private String name;

    @Valid
    private SettingsRequest settingsRequest;

    public PresetCreateRequest(final String name, final SettingsRequest settingsRequest) {
        this.name = name;
        this.settingsRequest = settingsRequest;
    }
}
