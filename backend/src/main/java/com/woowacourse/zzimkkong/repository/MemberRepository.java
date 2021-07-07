package com.woowacourse.zzimkkong.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.woowacourse.zzimkkong.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
