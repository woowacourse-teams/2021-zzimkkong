package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.MemberSaveRequestDto;
import com.woowacourse.zzimkkong.dto.MemberSaveResponseDto;
import com.woowacourse.zzimkkong.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<Void> join(@RequestBody @Valid MemberSaveRequestDto memberSaveRequestDto) {
        MemberSaveResponseDto memberSaveResponseDto = memberService.saveMember(memberSaveRequestDto);
        return ResponseEntity.created(URI.create("/api/members/" + memberSaveResponseDto.getId())).build();
    }
}
