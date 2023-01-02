package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.config.logaspect.LogMethodExecutionTime;
import com.woowacourse.zzimkkong.domain.LoginEmail;
import com.woowacourse.zzimkkong.dto.member.*;
import com.woowacourse.zzimkkong.dto.member.oauth.OauthMemberSaveRequest;
import com.woowacourse.zzimkkong.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@LogMethodExecutionTime(group = "controller")
@RestController
@RequestMapping("/api/members")
@Validated
public class MemberController {
    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<Void> join(@RequestBody @Valid final MemberSaveRequest memberSaveRequest) {
        MemberSaveResponse memberSaveResponse = memberService.saveMember(memberSaveRequest);
        return ResponseEntity
                .created(URI.create("/api/members/" + memberSaveResponse.getId()))
                .build();
    }

    @PostMapping("/oauth")
    public ResponseEntity<Void> joinByOauth(@RequestBody @Valid final OauthMemberSaveRequest oauthMemberSaveRequest) {
        MemberSaveResponse memberSaveResponse = memberService.saveMemberByOauth(oauthMemberSaveRequest);
        return ResponseEntity
                .created(URI.create("/api/members/" + memberSaveResponse.getId()))
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<MemberFindResponse> findMember(@LoginEmail final LoginUserEmail loginUserEmail) {
        MemberFindResponse memberFindResponse = memberService.findMember(loginUserEmail);
        return ResponseEntity.ok().body(memberFindResponse);
    }

    @PutMapping("/me")
    public ResponseEntity<MemberFindResponse> updateMember(
            @LoginEmail final LoginUserEmail loginUserEmail,
            @RequestBody @Valid final MemberUpdateRequest memberUpdateRequest) {
        memberService.updateMember(loginUserEmail, memberUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMember(@LoginEmail final LoginUserEmail loginUserEmail) {
        memberService.deleteMember(loginUserEmail);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/validations/email")
    public ResponseEntity<Void> validateEmail(@RequestBody @Valid final EmailValidationRequest emailValidationRequest) {
        memberService.validateDuplicateEmail(emailValidationRequest.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/validations/username")
    public ResponseEntity<Void> validateUserName(@RequestBody @Valid final UserNameValidationRequest userNameValidationRequest) {
        memberService.validateDuplicateUserName(userNameValidationRequest.getUserName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/emojis")
    public ResponseEntity<ProfileEmojisResponse> getEmojis() {
        ProfileEmojisResponse profileEmojis = memberService.getProfileEmojis();
        return ResponseEntity.ok().body(profileEmojis);
    }
}
