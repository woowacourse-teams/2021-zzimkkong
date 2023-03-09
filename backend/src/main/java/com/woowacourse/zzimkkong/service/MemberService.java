package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.OauthProvider;
import com.woowacourse.zzimkkong.domain.ProfileEmoji;
import com.woowacourse.zzimkkong.dto.member.*;
import com.woowacourse.zzimkkong.dto.member.oauth.OauthMemberSaveRequest;
import com.woowacourse.zzimkkong.exception.member.*;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
        validateDuplicateUserName(memberSaveRequest.getUserName());

        String password = passwordEncoder.encode(memberSaveRequest.getPassword());
        Member member = Member.builder()
                .email(memberSaveRequest.getEmail())
                .userName(memberSaveRequest.getUserName())
                .emoji(memberSaveRequest.getEmoji())
                .password(password)
                .build();
        Member saveMember = members.save(member);
        return MemberSaveResponse.from(saveMember);
    }

    public MemberSaveResponse saveMemberByOauth(final OauthMemberSaveRequest oauthMemberSaveRequest) {
        String email = oauthMemberSaveRequest.getEmail();
        OauthProvider oauthProvider = OauthProvider.valueOfWithIgnoreCase(oauthMemberSaveRequest.getOauthProvider());

        validateDuplicateEmail(email);
        validateDuplicateUserName(oauthMemberSaveRequest.getUserName());

        Member member = Member.builder()
                .email(email)
                .userName(oauthMemberSaveRequest.getUserName())
                .emoji(oauthMemberSaveRequest.getEmoji())
                .oauthProvider(oauthProvider)
                .build();
        Member saveMember = members.save(member);
        return MemberSaveResponse.from(saveMember);
    }

    @Transactional(readOnly = true)
    public void validateDuplicateEmail(final String email) {
        if (members.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }
    }

    @Transactional(readOnly = true)
    public void validateDuplicateUserName(final String userName) {
        if (members.existsByUserName(userName)) {
            throw new DuplicateUserNameException();
        }
    }

    @Transactional(readOnly = true)
    public MemberFindResponse findMember(final LoginUserEmail loginUserEmail) {
        Member member = members.findByEmail(loginUserEmail.getEmail())
                .orElseThrow(NoSuchMemberException::new);
        return MemberFindResponse.from(member);
    }

    public void updateMember(final LoginUserEmail loginUserEmail, final MemberUpdateRequest memberUpdateRequest) {
        Member member = members.findByEmail(loginUserEmail.getEmail())
                .orElseThrow(NoSuchMemberException::new);

        validateDuplicateUserName(memberUpdateRequest.getUserName());

        member.update(memberUpdateRequest);
    }

    public void deleteMember(final LoginUserEmail loginUserEmail) {
        Member member = members.findByEmail(loginUserEmail.getEmail())
                .orElseThrow(NoSuchMemberException::new);
        boolean hasAnyReservations = reservations.existsByMemberAndEndTimeAfter(member, LocalDateTime.now());
        if (hasAnyReservations) {
            throw new ReservationExistsOnMemberException();
        }

        members.delete(member);
    }

    @Transactional(readOnly = true)
    public ProfileEmojisResponse getProfileEmojis() {
        return ProfileEmojisResponse.from(List.of(ProfileEmoji.values()));
    }

    public void changePassword(LoginUserEmail loginUserEmail, ChangePasswordRequest changePasswordRequest) {
        Member member = members.findByEmail(loginUserEmail.getEmail())
                .orElseThrow(NoSuchMemberException::new);
        validateOldPassword(member, changePasswordRequest);
        validateConfirmationPassword(changePasswordRequest);

        String newPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());
        member.updatePassword(newPassword);
    }

    private void validateOldPassword(Member member, ChangePasswordRequest changePasswordRequest) {
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), member.getPassword())) {
            throw new PasswordMismatchException();
        }
    }

    private void validateConfirmationPassword(ChangePasswordRequest changePasswordRequest) {
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getNewPasswordConfirm())) {
            throw new ConfirmationNewPasswordMismatchException();
        }
    }
}
