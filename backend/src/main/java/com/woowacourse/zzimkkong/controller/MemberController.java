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
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.net.URI;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.*;
import static com.woowacourse.zzimkkong.dto.ValidatorMessage.NAME_MESSAGE;

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

    @GetMapping
    public ResponseEntity<Void> validateMemberData(
            @RequestParam(required = false)
            @Email(message = EMAIL_MESSAGE) final String email,
            @RequestParam(required = false)
            @Pattern(regexp = NAMING_FORMAT, message = NAME_MESSAGE) final String userName) {
        memberService.validateDuplicateEmail(email);
        memberService.validateDuplicateUserName(userName);
        return ResponseEntity.ok().build();
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

    @GetMapping("/emojis")
    public ResponseEntity<ProfileEmojisResponse> getEmojis() {
        ProfileEmojisResponse profileEmojis = memberService.getProfileEmojis();
        return ResponseEntity.ok().body(profileEmojis);
    }
}
