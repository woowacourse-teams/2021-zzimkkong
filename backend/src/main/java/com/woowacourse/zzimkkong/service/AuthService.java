package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.LoginRequest;
import com.woowacourse.zzimkkong.exception.LoginDataMismatchException;
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
    public void login(LoginRequest loginRequest) {
        Member findMember = memberRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(LoginDataMismatchException::new);

        if (!comparePassword(findMember.getPassword(), loginRequest.getPassword())) {
            throw new LoginDataMismatchException();
        }
    }

    private boolean comparePassword(String real, String input) {
        return real.equals(input);
    }
}
