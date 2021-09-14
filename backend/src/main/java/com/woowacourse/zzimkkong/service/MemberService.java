package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.OauthProvider;
import com.woowacourse.zzimkkong.domain.oauth.OauthUserInfo;
import com.woowacourse.zzimkkong.dto.member.MemberSaveRequest;
import com.woowacourse.zzimkkong.dto.member.MemberSaveResponse;
import com.woowacourse.zzimkkong.dto.member.oauth.OauthMemberSaveRequest;
import com.woowacourse.zzimkkong.dto.member.oauth.OauthReadyResponse;
import com.woowacourse.zzimkkong.exception.member.DuplicateEmailException;
import com.woowacourse.zzimkkong.infrastructure.oauth.OauthHandler;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberService {
    private final MemberRepository members;
    private final PasswordEncoder passwordEncoder;
    private final OauthHandler oauthHandler;

    public MemberService(final MemberRepository members,
                         final PasswordEncoder passwordEncoder,
                         final OauthHandler oauthHandler) {
        this.members = members;
        this.passwordEncoder = passwordEncoder;
        this.oauthHandler = oauthHandler;
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
    public OauthReadyResponse getUserInfoFromOauth(final OauthProvider oauthProvider, final String code) {
        OauthUserInfo userInfo = oauthHandler.getUserInfoFromCode(oauthProvider, code);
        String email = userInfo.getEmail();

        validateDuplicateEmail(email);

        return OauthReadyResponse.of(email, oauthProvider);
    }

    public MemberSaveResponse saveMemberByOauth(final OauthMemberSaveRequest oauthMemberSaveRequest) {
        validateDuplicateEmail(oauthMemberSaveRequest.getEmail());

        Member member = new Member(
                oauthMemberSaveRequest.getEmail(),
                oauthMemberSaveRequest.getOrganization(),
                OauthProvider.valueOfWithIgnoreCase(oauthMemberSaveRequest.getOauthProvider())
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
