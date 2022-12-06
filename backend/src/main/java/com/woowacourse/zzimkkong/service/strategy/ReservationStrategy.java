package com.woowacourse.zzimkkong.service.strategy;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
import com.woowacourse.zzimkkong.dto.reservation.ReservationAuthenticationDto;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateDto;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.repository.MemberRepository;

public abstract class ReservationStrategy {
    protected MemberRepository members;

    public ReservationStrategy(final MemberRepository members) {
        this.members = members;
    }

    public abstract boolean supports(UserType userType);

    public abstract void validateManagerOfMap(final Map map, final LoginUserEmail loginUserEmail);

    public abstract void validateOwnerOfReservation(final Reservation reservation, final String password, final LoginUserEmail loginUserEmail);

    public abstract boolean isManager();

    protected abstract Reservation buildReservation(
            final Space space,
            final ReservationTime reservationTime,
            final ReservationCreateDto reservationCreateDto);

    public Reservation createReservation(final Map map, final ReservationCreateDto reservationCreateDto) {
        LoginUserEmail loginUserEmail = reservationCreateDto.getLoginUserEmail();
        validateManagerOfMap(map, loginUserEmail);

        Long spaceId = reservationCreateDto.getSpaceId();
        Space space = map.findSpaceById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);

        ReservationTime reservationTime = ReservationTime.of(
                reservationCreateDto.getStartDateTime(),
                reservationCreateDto.getEndDateTime(),
                map.getServiceZone(),
                isManager());

        return buildReservation(space, reservationTime, reservationCreateDto);
    }
}
