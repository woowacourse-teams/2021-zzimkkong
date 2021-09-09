package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.member.MemberSaveRequest;
import com.woowacourse.zzimkkong.dto.member.MemberSaveResponse;
import com.woowacourse.zzimkkong.dto.member.MemberUpdateRequest;
import com.woowacourse.zzimkkong.exception.member.DuplicateEmailException;
import com.woowacourse.zzimkkong.exception.member.ReservationExistsOnMemberException;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberService {
    private final MemberRepository members;
    private final PasswordEncoder passwordEncoder;

    public MemberService(final MemberRepository members,
                         final PasswordEncoder passwordEncoder) {
        this.members = members;
        this.passwordEncoder = passwordEncoder;
    }

    public MemberSaveResponse saveMember(final MemberSaveRequest memberSaveRequest) {
        validateDuplicateEmail(memberSaveRequest.getEmail());

        String password = passwordEncoder.encode(memberSaveRequest.getPassword());
        Member member = new Member(
                memberSaveRequest.getEmail(),
                password,
                memberSaveRequest.getOrganization()
        );
        Member saveMember = members.save(member);
        return MemberSaveResponse.from(saveMember);
    }

    @Transactional(readOnly = true)
    public void validateDuplicateEmail(final String email) {
        if (members.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }
    }

    public void updateMember(final Member member, final MemberUpdateRequest memberUpdateRequest) {
        member.update(memberUpdateRequest.getOrganization());
    }

    public void deleteMember(final Member manager) {
        boolean hasAnyReservations = members.existsReservationsByMember(manager);
        if (hasAnyReservations) {
            throw new ReservationExistsOnMemberException();
        }

        members.delete(manager);
    }
}
