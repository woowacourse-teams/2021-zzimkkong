package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

    @Query("select distinct m from Member m left outer join fetch m.maps where m.email = :email")
    Optional<Member> findByEmailWithFetchMaps(String email);

    Page<Member> findAll(Pageable pageable);

    @Query("select distinct m from Member m left outer join fetch m.presets where m.email = :email")
    Optional<Member> findByEmailWithFetchPresets(String email);
}
