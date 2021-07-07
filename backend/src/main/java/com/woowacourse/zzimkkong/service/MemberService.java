package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.MemberSaveRequestDto;
import com.woowacourse.zzimkkong.dto.MemberSaveResponseDto;
import com.woowacourse.zzimkkong.exception.DuplicateEmailException;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberSaveResponseDto saveMember(final MemberSaveRequestDto memberSaveRequestDto) {
        validateDuplicateEmail(memberSaveRequestDto.getEmail());

        Member member = new Member(
                memberSaveRequestDto.getEmail(),
                memberSaveRequestDto.getPassword(),
                memberSaveRequestDto.getOrganization()
        );
        Member saveMember = memberRepository.save(member);
        return new MemberSaveResponseDto(saveMember.getId());
    }

    public void validateDuplicateEmail(final String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }
    }
}
