package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.LoginRequest;
import com.woowacourse.zzimkkong.exception.NoSuchEmailException;
import com.woowacourse.zzimkkong.exception.PasswordMismatchException;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {
    private final MemberRepository memberRepository;

    public AuthService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // todo AccessToken이 담긴 Dto를 반환
    @Transactional(readOnly = true)
    public void login(LoginRequest loginRequest) {
        Member findMember = findMemberByEmailOrElseThrow(loginRequest.getEmail());

        validatePassword(findMember, loginRequest.getPassword());
    }

    private Member findMemberByEmailOrElseThrow(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(NoSuchEmailException::new);
    }

    private void validatePassword(Member findMember, String password) {
        if (!findMember.getPassword().equals(password)) {
            throw new PasswordMismatchException();
        }
    }
}
