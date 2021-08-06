package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.dto.space.SettingsRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.*;

public class PresetCreateRequest {
    @NotNull(message = EMPTY_MESSAGE)
    @Pattern(regexp = NAMING_FORMAT, message = NAME_MESSAGE)
    private String name;

    @Valid
    private SettingsRequest settingsRequest;

    public PresetCreateRequest() {

    }

    public PresetCreateRequest(String name, SettingsRequest settingsRequest) {
        this.name = name;
        this.settingsRequest = settingsRequest;
    }

    public String getName() {
        return name;
    }

    public SettingsRequest getSettingsRequest() {
        return settingsRequest;
    }
}
