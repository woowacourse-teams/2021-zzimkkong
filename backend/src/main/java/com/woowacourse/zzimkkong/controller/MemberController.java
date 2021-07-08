package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.MemberSaveRequestDto;
import com.woowacourse.zzimkkong.dto.MemberSaveResponseDto;
import com.woowacourse.zzimkkong.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<Void> join(@RequestBody @Valid final MemberSaveRequestDto memberSaveRequestDto) {
        MemberSaveResponseDto memberSaveResponseDto = memberService.saveMember(memberSaveRequestDto);
        return ResponseEntity.created(URI.create("/api/members/" + memberSaveResponseDto.getId())).build();
    }

    @GetMapping
    public ResponseEntity<Void> validateEmail(@RequestParam final String email) {
        memberService.validateDuplicateEmail(email);
        return ResponseEntity.ok().build();
    }
}
