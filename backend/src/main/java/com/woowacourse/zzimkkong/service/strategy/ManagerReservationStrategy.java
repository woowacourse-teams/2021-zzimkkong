package com.woowacourse.zzimkkong.service.strategy;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateDto;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;
import com.woowacourse.zzimkkong.exception.member.NoSuchMemberException;
import com.woowacourse.zzimkkong.exception.reservation.InvalidManagerReservationException;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import org.springframework.stereotype.Component;

@Component
public class ManagerReservationStrategy extends ReservationStrategy {
    public ManagerReservationStrategy(final MemberRepository members) {
        super(members);
    }

    @Override
    public boolean supports(final ReservationType reservationType) {
        return ReservationType.MANAGER.equals(reservationType);
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
        if (reservationCreateDto.isManagerReservationForLoginUser()) {
            Member member = members.findByEmail(reservationCreateDto.getEmail())
                    .orElseThrow(NoSuchMemberException::new);
            return Reservation.builder()
                    .reservationTime(reservationTime)
                    .member(member)
                    .userName(member.getUserName())
                    .description(reservationCreateDto.getDescription())
                    .space(space)
                    .build();
        }
        if (reservationCreateDto.isManagerReservationForNonLoginUser()) {
            return Reservation.builder()
                    .reservationTime(reservationTime)
                    .password(reservationCreateDto.getPassword())
                    .userName(reservationCreateDto.getName())
                    .description(reservationCreateDto.getDescription())
                    .space(space)
                    .build();
        }
        throw new InvalidManagerReservationException();
    }
}
