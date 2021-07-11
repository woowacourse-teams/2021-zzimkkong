package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.LoginRequest;
import com.woowacourse.zzimkkong.dto.TokenResponse;
import com.woowacourse.zzimkkong.exception.NoSuchMemberException;
import com.woowacourse.zzimkkong.exception.PasswordMismatchException;
import com.woowacourse.zzimkkong.infrastructure.JwtUtils;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {
    private final MemberRepository memberRepository;
    private final JwtUtils jwtUtils;

    public AuthService(final MemberRepository memberRepository, final JwtUtils jwtUtils) {
        this.memberRepository = memberRepository;
        this.jwtUtils = jwtUtils;
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest loginRequest) {
        Member findMember = memberRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(NoSuchMemberException::new);

        validatePassword(findMember, loginRequest.getPassword());

        String token = issueToken(findMember);

        return TokenResponse.of(token);
    }

    private String issueToken(Member findMember) {
        return jwtUtils.createToken(JwtUtils.payloadBuilder()
                .setSubject(findMember.getEmail())
                .build());
    }

    private void validatePassword(Member findMember, String password) {
        if (!findMember.checkPassword(password)) {
            throw new PasswordMismatchException();
        }
    }
}
