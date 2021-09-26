package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.OauthProvider;
import com.woowacourse.zzimkkong.dto.member.MemberFindResponse;
import com.woowacourse.zzimkkong.dto.member.MemberSaveRequest;
import com.woowacourse.zzimkkong.dto.member.MemberSaveResponse;
import com.woowacourse.zzimkkong.dto.member.MemberUpdateRequest;
import com.woowacourse.zzimkkong.dto.member.oauth.OauthMemberSaveRequest;
import com.woowacourse.zzimkkong.exception.member.DuplicateEmailException;
import com.woowacourse.zzimkkong.exception.member.NoSuchMemberException;
import com.woowacourse.zzimkkong.exception.member.ReservationExistsOnMemberException;
import com.woowacourse.zzimkkong.infrastructure.auth.LoginEmail;
import com.woowacourse.zzimkkong.infrastructure.oauth.OauthHandler;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberService {
    private final MemberRepository members;
    private final ReservationRepository reservations;
    private final PasswordEncoder passwordEncoder;

    public MemberService(final MemberRepository members,
                         final ReservationRepository reservations,
                         final PasswordEncoder passwordEncoder) {
        this.members = members;
        this.reservations = reservations;
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

    public MemberSaveResponse saveMemberByOauth(final OauthMemberSaveRequest oauthMemberSaveRequest) {
        String email = oauthMemberSaveRequest.getEmail();
        OauthProvider oauthProvider = OauthProvider.valueOfWithIgnoreCase(oauthMemberSaveRequest.getOauthProvider());

        validateDuplicateEmail(email);

        Member member = new Member(
                email,
                oauthMemberSaveRequest.getOrganization(),
                oauthProvider
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

    public MemberFindResponse findMember(final LoginEmail loginEmail) {
        Member member = members.findByEmail(loginEmail.getEmail()).orElseThrow(NoSuchMemberException::new);
        return MemberFindResponse.from(member);
    }

    public void updateMember(final LoginEmail loginEmail, final MemberUpdateRequest memberUpdateRequest) {
        Member member = members.findByEmail(loginEmail.getEmail()).orElseThrow(NoSuchMemberException::new);
        member.update(memberUpdateRequest.getOrganization());
    }

    public void deleteMember(final LoginEmail loginEmail) {
        Member manager = members.findByEmail(loginEmail.getEmail()).orElseThrow(NoSuchMemberException::new);
        boolean hasAnyReservations = reservations.existsReservationsByMemberFromToday(manager);
        if (hasAnyReservations) {
            throw new ReservationExistsOnMemberException();
        }

        members.delete(manager);
    }
}
