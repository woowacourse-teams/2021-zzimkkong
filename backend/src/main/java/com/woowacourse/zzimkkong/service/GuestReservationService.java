package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateResponse;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateUpdateWithPasswordRequest;
import com.woowacourse.zzimkkong.dto.reservation.ReservationPasswordAuthenticationRequest;
import com.woowacourse.zzimkkong.dto.reservation.ReservationResponse;
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
public class GuestReservationService extends ReservationService {
    public GuestReservationService(
            final MapRepository maps,
            final SpaceRepository spaces,
            final ReservationRepository reservations) {
        super(maps, spaces, reservations);
    }

    public ReservationCreateResponse saveReservation(
            final Long mapId,
            final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
        validateMapExistence(mapId);

        validateTime(reservationCreateUpdateWithPasswordRequest);
        Space space = spaces.findById(reservationCreateUpdateWithPasswordRequest.getSpaceId())
                .orElseThrow(NoSuchSpaceException::new);
        validateAvailability(space, reservationCreateUpdateWithPasswordRequest);

        Reservation reservation = reservations.save(
                new Reservation.Builder()
                        .startTime(reservationCreateUpdateWithPasswordRequest.getStartDateTime())
                        .endTime(reservationCreateUpdateWithPasswordRequest.getEndDateTime())
                        .password(reservationCreateUpdateWithPasswordRequest.getPassword())
                        .userName(reservationCreateUpdateWithPasswordRequest.getName())
                        .description(reservationCreateUpdateWithPasswordRequest.getDescription())
                        .space(space)
                        .build());

        return ReservationCreateResponse.from(reservation);
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

    public void updateReservation(
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
    }

    public void deleteReservation(
            final Long mapId,
            final Long reservationId,
            final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        validateMapExistence(mapId);

        Reservation reservation = reservations
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);
        checkCorrectPassword(reservation, reservationPasswordAuthenticationRequest.getPassword());
        reservations.delete(reservation);
    }

    private void checkCorrectPassword(final Reservation reservation, final String password) {
        if (reservation.isWrongPassword(password)) {
            throw new ReservationPasswordException();
        }
    }
}
