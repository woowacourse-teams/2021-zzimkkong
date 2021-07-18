package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateUpdateWithPasswordRequest;
import com.woowacourse.zzimkkong.dto.reservation.ReservationPasswordAuthenticationRequest;
import com.woowacourse.zzimkkong.dto.reservation.ReservationResponse;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.exception.reservation.NoSuchReservationException;
import com.woowacourse.zzimkkong.exception.reservation.ReservationPasswordException;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserReservationService extends ReservationService {
    public UserReservationService(
            final MapRepository maps,
            final SpaceRepository spaces,
            final ReservationRepository reservations) {
        super(maps, spaces, reservations);
    }

    @Transactional(readOnly = true)
    public ReservationResponse findReservation(
            final Long mapId,
            final Long reservationId,
            final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        validateMapExistence(mapId);

        Reservation reservation = reservations
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);
        checkCorrectPassword(reservation, reservationPasswordAuthenticationRequest.getPassword());
        return ReservationResponse.from(reservation);
    }

    public SlackResponse updateReservation(
            final Long mapId,
            final Long reservationId,
            final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
        validateMapExistence(mapId);
        validateTime(reservationCreateUpdateWithPasswordRequest);

        Space space = spaces.findById(reservationCreateUpdateWithPasswordRequest.getSpaceId())
                .orElseThrow(NoSuchSpaceException::new);
        Reservation reservation = reservations
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);

        checkCorrectPassword(reservation, reservationCreateUpdateWithPasswordRequest.getPassword());
        doDirtyCheck(reservation, reservationCreateUpdateWithPasswordRequest, space);
        validateAvailability(space, reservationCreateUpdateWithPasswordRequest, reservation);

        reservation.update(reservationCreateUpdateWithPasswordRequest, space);
        reservations.save(reservation);
        return SlackResponse.from(reservation);
    }

    public SlackResponse deleteReservation(
            final Long mapId,
            final Long reservationId,
            final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        validateMapExistence(mapId);

        Reservation reservation = reservations
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);
        checkCorrectPassword(reservation, reservationPasswordAuthenticationRequest.getPassword());
        reservations.delete(reservation);
        return SlackResponse.from(reservation);
    }

    private void checkCorrectPassword(final Reservation reservation, final String password) {
        if (reservation.isWrongPassword(password)) {
            throw new ReservationPasswordException();
        }
    }
}
