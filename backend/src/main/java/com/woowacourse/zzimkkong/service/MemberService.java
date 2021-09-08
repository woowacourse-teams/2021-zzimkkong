package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.OAuthProvider;
import com.woowacourse.zzimkkong.dto.member.OAuthMemberSaveRequest;
import com.woowacourse.zzimkkong.dto.member.MemberSaveRequest;
import com.woowacourse.zzimkkong.dto.member.MemberSaveResponse;
import com.woowacourse.zzimkkong.dto.member.OAuthReadyResponse;
import com.woowacourse.zzimkkong.exception.member.DuplicateEmailException;
import com.woowacourse.zzimkkong.infrastructure.oauth.OAuthHandler;
import com.woowacourse.zzimkkong.domain.oauth.OAuthUserInfo;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberService {
    private final MemberRepository members;
    private final PasswordEncoder passwordEncoder;
    private final OAuthHandler oAuthHandler;

    public MemberService(final MemberRepository members,
                         final PasswordEncoder passwordEncoder,
                         final OAuthHandler oAuthHandler) {
        this.members = members;
        this.passwordEncoder = passwordEncoder;
        this.oAuthHandler = oAuthHandler;
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
    public OAuthReadyResponse getUserInfoFromOAuth(OAuthProvider oauthProvider, String code) {
        OAuthUserInfo userInfo = oAuthHandler.getUserInfoFromCode(oauthProvider, code);
        String email = userInfo.getEmail();

        validateDuplicateEmail(email);

        return OAuthReadyResponse.of(email, oauthProvider);
    }

    public MemberSaveResponse saveMemberByOAuth(OAuthMemberSaveRequest oAuthMemberSaveRequest) {
        validateDuplicateEmail(oAuthMemberSaveRequest.getEmail());

        Member member = new Member(
                oAuthMemberSaveRequest.getEmail(),
                oAuthMemberSaveRequest.getOrganization(),
                OAuthProvider.valueOf(oAuthMemberSaveRequest.getOAuthProvider())
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
}
