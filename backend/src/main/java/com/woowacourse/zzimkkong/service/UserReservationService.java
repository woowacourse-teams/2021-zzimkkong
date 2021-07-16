package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateUpdateRequest;
import com.woowacourse.zzimkkong.dto.reservation.ReservationPasswordAuthenticationRequest;
import com.woowacourse.zzimkkong.dto.reservation.ReservationResponse;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.exception.reservation.NoSuchReservationException;
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
            final MapRepository mapRepository,
            final SpaceRepository spaceRepository,
            final ReservationRepository reservationRepository) {
        super(mapRepository, spaceRepository, reservationRepository);
    }

    @Override
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

    @Override
    public ReservationResponse findReservation(
            final Long mapId,
            final Long reservationId,
            final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        validateMapExistence(mapId);

        Reservation reservation = reservations
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);
        checkCorrectPassword(reservation, reservationPasswordAuthenticationRequest.getPassword());
        return ReservationResponse.of(reservation);
    }

    @Override
    public SlackResponse updateReservation(
            final Long mapId,
            final Long reservationId,
            final ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
        validateMapExistence(mapId);
        validateTime(reservationCreateUpdateRequest);

        Space space = spaces.findById(reservationCreateUpdateRequest.getSpaceId())
                .orElseThrow(NoSuchSpaceException::new);
        Reservation reservation = reservations
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);

        checkCorrectPassword(reservation, reservationCreateUpdateRequest.getPassword());
        doDirtyCheck(reservation, reservationCreateUpdateRequest, space);
        validateAvailability(space, reservationCreateUpdateRequest, reservation);

        reservation.update(reservationCreateUpdateRequest, space);
        reservations.save(reservation);
        return SlackResponse.from(reservation);
    }
}
