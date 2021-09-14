package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Member;

public interface ReservationRepositoryCustom {
    boolean existsReservationsByMemberFromToday(Member member);
}
