package com.woowacourse.zzimkkong.service.strategy;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateDto;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;
import com.woowacourse.zzimkkong.exception.reservation.InvalidNonLoginReservationException;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import org.springframework.stereotype.Component;

@Component
public class ManagerNonLoginGuestReservationStrategy extends ReservationStrategy {
    public ManagerNonLoginGuestReservationStrategy(final MemberRepository members) {
        super(members);
    }

    @Override
    public boolean supports(final ReservationType reservationType) {
        return ReservationType.NON_LOGIN_MANAGER.equals(reservationType);
    }

    @Override
    public void validateManagerOfMap(final Map map, final LoginUserEmail loginUserEmail) {
        Member manager = map.getMember();
        if (!manager.hasEmail(loginUserEmail.getEmail())) {
            throw new NoAuthorityOnMapException();
        }
    }

    @Override
    public void validateOwnerOfReservation(final Reservation reservation, final String password, final LoginUserEmail loginUserEmail) {
        // manager는 비밀번호 확인과정이 없으므로 생략
    }

    @Override
    public boolean isManager() {
        return true;
    }

    @Override
    protected Reservation buildReservation(
            final Space space,
            final ReservationTime reservationTime,
            final ReservationCreateDto reservationCreateDto) {
        if (reservationCreateDto.isInvalidNonLoginGuestReservation()) {
            throw new InvalidNonLoginReservationException();
        }
        return Reservation.builder()
                .reservationTime(reservationTime)
                .userName(reservationCreateDto.getName())
                .password(reservationCreateDto.getPassword())
                .description(reservationCreateDto.getDescription())
                .space(space)
                .build();
    }
}
