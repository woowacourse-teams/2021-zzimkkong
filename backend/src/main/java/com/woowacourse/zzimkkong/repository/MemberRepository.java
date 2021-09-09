package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

    @Query("SELECT COUNT(r) > 0 FROM Reservation r JOIN r.space s JOIN s.map m WHERE m.member = :member")
    boolean existsReservationsByMember(@Param("member") Member member);
}
