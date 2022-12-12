package com.woowacourse.zzimkkong.service.strategy;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateDto;
import com.woowacourse.zzimkkong.exception.reservation.ReservationOwnershipException;
import com.woowacourse.zzimkkong.exception.reservation.ReservationPasswordException;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import org.springframework.stereotype.Component;

@Component
public class NonLoginGuestReservationStrategy extends ReservationStrategy {
    public NonLoginGuestReservationStrategy(final MemberRepository members) {
        super(members);
    }

    @Override
    public boolean supports(final ReservationType reservationType) {
        return ReservationType.NON_LOGIN_GUEST.equals(reservationType);
    }

    @Override
    public void validateManagerOfMap(final Map map, final LoginUserEmail loginUserEmail) {
        // 비로그인 예약은 맵의 관리자 확인과정 생략
    }

    @Override
    public void validateOwnerOfReservation(final Reservation reservation, final String password, final LoginUserEmail loginUserEmail) {
        if (reservation.hasMember()) {
            throw new ReservationOwnershipException();
        }
        if (reservation.isWrongPassword(password)) {
            throw new ReservationPasswordException();
        }
    }

    @Override
    public boolean isManager() {
        return false;
    }

    @Override
    protected Reservation buildReservation(
            final Space space,
            final ReservationTime reservationTime,
            final ReservationCreateDto reservationCreateDto) {
        return Reservation.builder()
                .reservationTime(reservationTime)
                .userName(reservationCreateDto.getName())
                .password(reservationCreateDto.getPassword())
                .description(reservationCreateDto.getDescription())
                .space(space)
                .build();
    }
}
