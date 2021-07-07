package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.MemberSaveRequestDto;
import com.woowacourse.zzimkkong.dto.MemberSaveResponseDto;
import com.woowacourse.zzimkkong.exception.DuplicateEmailException;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberService {
    private MemberRepository memberRepository;

    public MemberService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberSaveResponseDto saveMember(final MemberSaveRequestDto memberSaveRequestDto) {
        if (memberRepository.existsByEmail(memberSaveRequestDto.getEmail())) {
            throw new DuplicateEmailException();
        }

        Member member = new Member(
                memberSaveRequestDto.getEmail(),
                memberSaveRequestDto.getPassword(),
                memberSaveRequestDto.getOrganization()
        );
        Member saveMember = memberRepository.save(member);
        return new MemberSaveResponseDto(saveMember.getId());
    }
}
