package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Manager;
import com.woowacourse.zzimkkong.dto.member.*;
import com.woowacourse.zzimkkong.dto.member.oauth.OauthMemberSaveRequest;
import com.woowacourse.zzimkkong.infrastructure.auth.LoginEmail;
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
            @Manager final LoginEmail loginEmail) {
        PresetCreateResponse presetCreateResponse = presetService.savePreset(presetCreateRequest, loginEmail);
        return ResponseEntity
                .created(URI.create("/api/managers/presets/" + presetCreateResponse.getId()))
                .build();
    }

    @GetMapping("/presets")
    public ResponseEntity<PresetFindAllResponse> findAllPresets(@Manager final LoginEmail loginEmail) {
        PresetFindAllResponse presetFindAllResponse = presetService.findAllPresets(loginEmail);
        return ResponseEntity.ok().body(presetFindAllResponse);
    }

    @DeleteMapping("/presets/{presetId}")
    public ResponseEntity<Void> deletePreset(
            @PathVariable final Long presetId,
            @Manager final LoginEmail loginEmail) {
        presetService.deletePreset(presetId, loginEmail);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<MemberFindResponse> findMember(@Manager final LoginEmail loginEmail) {
        MemberFindResponse memberFindResponse = memberService.findMember(loginEmail);
        return ResponseEntity.ok().body(memberFindResponse);
    }

    @PutMapping("/me")
    public ResponseEntity<MemberFindResponse> updateMember(
            @Manager final LoginEmail loginEmail,
            @RequestBody @Valid final MemberUpdateRequest memberUpdateRequest) {
        memberService.updateMember(loginEmail, memberUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMember(@Manager final LoginEmail loginEmail) {
        memberService.deleteMember(loginEmail);
        return ResponseEntity.noContent().build();
    }
}
