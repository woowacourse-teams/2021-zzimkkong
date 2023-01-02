package com.woowacourse.zzimkkong.service.strategy;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateDto;
import com.woowacourse.zzimkkong.exception.member.NoSuchMemberException;
import com.woowacourse.zzimkkong.exception.reservation.NoAuthorityOnReservationException;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import org.springframework.stereotype.Component;

@Component
public class LoginGuestReservationStrategy extends ReservationStrategy {

    public LoginGuestReservationStrategy(final MemberRepository members) {
        super(members);
    }

    @Override
    public boolean supports(final ReservationType reservationType) {
        return ReservationType.LOGIN_GUEST.equals(reservationType);
    }

    @Override
    public void validateManagerOfMap(final Map map, final LoginUserEmail loginUserEmail) {
        // 로그인 예약은 맵의 관리자 확인과정 생략
    }

    @Override
    public void validateOwnerOfReservation(final Reservation reservation, final String password, final LoginUserEmail loginUserEmail) {
        if (loginUserEmail.exists()) {
            Member member = members.findByEmail(loginUserEmail.getEmail())
                    .orElseThrow(NoSuchMemberException::new);
            if (reservation.isNotOwnedBy(member)) {
                throw new NoAuthorityOnReservationException();
            }
            return;
        }
        throw new NoAuthorityOnReservationException();
    }

    @Override
    public boolean isManager() {
        return false;
    }

    @Override
    protected Reservation buildReservation(final Space space, final ReservationTime reservationTime, final ReservationCreateDto reservationCreateDto) {
        LoginUserEmail loginUserEmail = reservationCreateDto.getLoginUserEmail();
        if (!loginUserEmail.exists()) {
            throw new NoAuthorityOnReservationException();
        }

        Member member = members.findByEmail(loginUserEmail.getEmail())
                .orElseThrow(NoSuchMemberException::new);
        return Reservation.builder()
                .reservationTime(reservationTime)
                .member(member)
                .userName(member.getUserName())
                .description(reservationCreateDto.getDescription())
                .space(space)
                .build();
    }
}
