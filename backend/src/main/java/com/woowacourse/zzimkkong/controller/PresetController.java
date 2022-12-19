package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime;
import com.woowacourse.zzimkkong.domain.LoginEmail;
import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
import com.woowacourse.zzimkkong.dto.member.PresetCreateRequest;
import com.woowacourse.zzimkkong.dto.member.PresetCreateResponse;
import com.woowacourse.zzimkkong.dto.member.PresetFindAllResponse;
import com.woowacourse.zzimkkong.service.PresetService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@LogMethodExecutionTime(group = "controller")
@RestController
@RequestMapping("/api/managers")
@Validated
public class PresetController {
    private final PresetService presetService;

    public PresetController(final PresetService presetService) {
        this.presetService = presetService;
    }

    @PostMapping("/presets")
    public ResponseEntity<Void> createPreset(
            @RequestBody @Valid final PresetCreateRequest presetCreateRequest,
            @LoginEmail final LoginUserEmail loginUserEmail) {
        PresetCreateResponse presetCreateResponse = presetService.savePreset(presetCreateRequest, loginUserEmail);
        return ResponseEntity
                .created(URI.create("/api/managers/presets/" + presetCreateResponse.getId()))
                .build();
    }

    @GetMapping("/presets")
    public ResponseEntity<PresetFindAllResponse> findAllPresets(@LoginEmail final LoginUserEmail loginUserEmail) {
        PresetFindAllResponse presetFindAllResponse = presetService.findAllPresets(loginUserEmail);
        return ResponseEntity.ok().body(presetFindAllResponse);
    }

    @DeleteMapping("/presets/{presetId}")
    public ResponseEntity<Void> deletePreset(
            @PathVariable final Long presetId,
            @LoginEmail final LoginUserEmail loginUserEmail) {
        presetService.deletePreset(presetId, loginUserEmail);

        return ResponseEntity.noContent().build();
    }
}
