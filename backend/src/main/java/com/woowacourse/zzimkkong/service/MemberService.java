package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.MemberSaveRequest;
import com.woowacourse.zzimkkong.dto.MemberSaveResponse;
import com.woowacourse.zzimkkong.exception.DuplicateEmailException;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberSaveResponse saveMember(final MemberSaveRequest memberSaveRequest) {
        validateDuplicateEmail(memberSaveRequest.getEmail());

        Member member = new Member(
                memberSaveRequest.getEmail(),
                memberSaveRequest.getPassword(),
                memberSaveRequest.getOrganization()
        );
        Member saveMember = memberRepository.save(member);
        return new MemberSaveResponse(saveMember.getId());
    }

    public void validateDuplicateEmail(final String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }
    }
}
