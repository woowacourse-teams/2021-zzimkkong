package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Member;

import java.time.LocalDateTime;

public interface ReservationRepositoryCustom {
    boolean existsByMemberAndEndTimeAfter(final Member member, final LocalDateTime now);
}
