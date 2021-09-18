package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Manager;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.member.*;
import com.woowacourse.zzimkkong.dto.member.oauth.OauthMemberSaveRequest;
import com.woowacourse.zzimkkong.service.MemberService;
import com.woowacourse.zzimkkong.service.PresetService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.net.URI;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.EMAIL_MESSAGE;
import static com.woowacourse.zzimkkong.dto.ValidatorMessage.EMPTY_MESSAGE;

@RestController
@RequestMapping("/api/managers")
@Validated
public class MemberController {
    private final MemberService memberService;
    private final PresetService presetService;

    public MemberController(final MemberService memberService, final PresetService presetService) {
        this.memberService = memberService;
        this.presetService = presetService;
    }

    @PostMapping
    public ResponseEntity<Void> join(@RequestBody @Valid final MemberSaveRequest memberSaveRequest) {
        MemberSaveResponse memberSaveResponse = memberService.saveMember(memberSaveRequest);
        return ResponseEntity
                .created(URI.create("/api/managers/" + memberSaveResponse.getId()))
                .build();
    }

    @PostMapping("/oauth")
    public ResponseEntity<Void> joinByOauth(@RequestBody @Valid final OauthMemberSaveRequest oauthMemberSaveRequest) {
        MemberSaveResponse memberSaveResponse = memberService.saveMemberByOauth(oauthMemberSaveRequest);
        return ResponseEntity
                .created(URI.create("/api/managers/" + memberSaveResponse.getId()))
                .build();
    }

    @GetMapping
    public ResponseEntity<Void> validateEmail(
            @RequestParam
            @NotBlank(message = EMPTY_MESSAGE)
            @Email(message = EMAIL_MESSAGE) final String email) {
        memberService.validateDuplicateEmail(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/presets")
    public ResponseEntity<Void> createPreset(
            @RequestBody @Valid final PresetCreateRequest presetCreateRequest,
            @Manager final Member manager) {
        PresetCreateResponse presetCreateResponse = presetService.savePreset(presetCreateRequest, manager);
        return ResponseEntity
                .created(URI.create("/api/managers/presets/" + presetCreateResponse.getId()))
                .build();
    }

    @GetMapping("/presets")
    public ResponseEntity<PresetFindAllResponse> findAllPresets(@Manager final Member manager) {
        PresetFindAllResponse presetFindAllResponse = presetService.findAllPresets(manager);
        return ResponseEntity.ok().body(presetFindAllResponse);
    }

    @DeleteMapping("/presets/{presetId}")
    public ResponseEntity<Void> deletePreset(
            @PathVariable final Long presetId,
            @Manager final Member manager) {
        presetService.deletePreset(presetId, manager);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<MemberFindResponse> findMember(@Manager final Member manager) {
        MemberFindResponse memberFindResponse = MemberFindResponse.from(manager);
        return ResponseEntity.ok().body(memberFindResponse);
    }

    @PutMapping("/me")
    public ResponseEntity<MemberFindResponse> updateMember(
            @Manager final Member manager,
            @RequestBody @Valid final MemberUpdateRequest memberUpdateRequest) {
        memberService.updateMember(manager, memberUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMember(@Manager final Member manager) {
        memberService.deleteMember(manager);
        return ResponseEntity.noContent().build();
    }
}
