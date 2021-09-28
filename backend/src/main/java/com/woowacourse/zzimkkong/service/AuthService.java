package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.OauthProvider;
import com.woowacourse.zzimkkong.domain.oauth.OauthUserInfo;
import com.woowacourse.zzimkkong.dto.member.LoginRequest;
import com.woowacourse.zzimkkong.dto.member.TokenResponse;
import com.woowacourse.zzimkkong.exception.authorization.OauthProviderMismatchException;
import com.woowacourse.zzimkkong.exception.member.NoSuchMemberException;
import com.woowacourse.zzimkkong.exception.member.NoSuchOAuthMemberException;
import com.woowacourse.zzimkkong.exception.member.PasswordMismatchException;
import com.woowacourse.zzimkkong.infrastructure.auth.JwtUtils;
import com.woowacourse.zzimkkong.infrastructure.oauth.OauthHandler;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
public class AuthService {
    private final MemberRepository members;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final OauthHandler oauthHandler;

    public AuthService(final MemberRepository members,
                       final JwtUtils jwtUtils,
                       final PasswordEncoder passwordEncoder,
                       final OauthHandler oauthHandler) {
        this.members = members;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.oauthHandler = oauthHandler;
    }

    @Transactional(readOnly = true)
    public TokenResponse login(final LoginRequest loginRequest) {
        Member findMember = members.findByEmail(loginRequest.getEmail())
                .orElseThrow(NoSuchMemberException::new);

        validatePassword(findMember, loginRequest.getPassword());

        String token = issueToken(findMember);

        return TokenResponse.from(token);
    }

    @Transactional(readOnly = true)
    public TokenResponse loginByOauth(final OauthProvider oauthProvider, final String code) {
        OauthUserInfo userInfoFromCode = oauthHandler.getUserInfoFromCode(oauthProvider, code);
        String email = userInfoFromCode.getEmail();

        Member member = members.findByEmail(email)
                .orElseThrow(() -> new NoSuchOAuthMemberException(email));

        validateOauthProvider(oauthProvider, member);

        String token = issueToken(member);
        return TokenResponse.from(token);
    }

    private String issueToken(final Member findMember) {
        Map<String, Object> payload = JwtUtils.payloadBuilder()
                .setSubject(findMember.getEmail())
                .build();

        return jwtUtils.createToken(payload);
    }

    private void validatePassword(final Member findMember, final String password) {
        if (!passwordEncoder.matches(password, findMember.getPassword())) {
            throw new PasswordMismatchException();
        }
    }

    private void validateOauthProvider(final OauthProvider oauthProvider, final Member member) {
        OauthProvider memberOauthProvider = member.getOauthProvider();
        if (!oauthProvider.equals(memberOauthProvider)) {
            throw OauthProviderMismatchException.from(memberOauthProvider);
        }
    }
}
