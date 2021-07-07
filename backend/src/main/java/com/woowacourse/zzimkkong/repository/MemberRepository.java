package com.woowacourse.zzimkkong.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.woowacourse.zzimkkong.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
}
