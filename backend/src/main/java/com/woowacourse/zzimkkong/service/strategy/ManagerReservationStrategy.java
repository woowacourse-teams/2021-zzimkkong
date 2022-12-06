package com.woowacourse.zzimkkong.service.strategy;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
import com.woowacourse.zzimkkong.dto.reservation.ReservationAuthenticationDto;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateDto;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import org.springframework.stereotype.Component;

@Component
public class ManagerReservationStrategy extends ReservationStrategy {
    public ManagerReservationStrategy(final MemberRepository members) {
        super(members);
    }

    @Override
    public boolean supports(final UserType userType) {
        return UserType.MANAGER.equals(userType);
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
        return Reservation.builder()
                .reservationTime(reservationTime)
                .password(reservationCreateDto.getPassword())
                .userName(reservationCreateDto.getName())
                .description(reservationCreateDto.getDescription())
                .space(space)
                .build();
    }
}
