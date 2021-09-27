package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Preset;
import com.woowacourse.zzimkkong.domain.Setting;
import com.woowacourse.zzimkkong.dto.member.PresetCreateResponse;
import com.woowacourse.zzimkkong.dto.member.PresetFindAllResponse;
import com.woowacourse.zzimkkong.dto.space.SettingsRequest;
import com.woowacourse.zzimkkong.dto.member.PresetCreateRequest;
import com.woowacourse.zzimkkong.exception.member.NoSuchMemberException;
import com.woowacourse.zzimkkong.exception.preset.NoSuchPresetException;
import com.woowacourse.zzimkkong.dto.member.LoginEmailDto;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import com.woowacourse.zzimkkong.repository.PresetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PresetService {
    private final MemberRepository members;
    private final PresetRepository presets;

    public PresetService(
            final MemberRepository members,
            final PresetRepository presets) {
        this.members = members;
        this.presets = presets;
    }

    public PresetCreateResponse savePreset(final PresetCreateRequest presetCreateRequest, final LoginEmailDto loginEmailDto) {
        SettingsRequest settingsRequest = presetCreateRequest.getSettingsRequest();
        Setting setting = Setting.builder()
                .availableStartTime(settingsRequest.getAvailableStartTime())
                .availableEndTime(settingsRequest.getAvailableEndTime())
                .reservationTimeUnit(settingsRequest.getReservationTimeUnit())
                .reservationMinimumTimeUnit(settingsRequest.getReservationMinimumTimeUnit())
                .reservationMaximumTimeUnit(settingsRequest.getReservationMaximumTimeUnit())
                .reservationEnable(settingsRequest.getReservationEnable())
                .enabledDayOfWeek(settingsRequest.getEnabledDayOfWeek())
                .build();

        Member manager = members.findByEmail(loginEmailDto.getEmail())
                .orElseThrow(NoSuchMemberException::new);
        Preset preset = presets.save(new Preset(presetCreateRequest.getName(), setting, manager));

        return PresetCreateResponse.from(preset);
    }

    @Transactional(readOnly = true)
    public PresetFindAllResponse findAllPresets(final LoginEmailDto loginEmailDto) {
        Member manager = members.findByEmail(loginEmailDto.getEmail())
                .orElseThrow(NoSuchMemberException::new);
        List<Preset> findPresets = manager.getPresets();
        return PresetFindAllResponse.from(findPresets);
    }

    public void deletePreset(final Long presetId, final LoginEmailDto loginEmailDto) {
        Member manager = members.findByEmail(loginEmailDto.getEmail())
                .orElseThrow(NoSuchMemberException::new);
        Preset preset = manager.findPresetById(presetId)
                .orElseThrow(NoSuchPresetException::new);

        presets.delete(preset);
    }
}
