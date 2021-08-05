package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.reservation.NoSuchReservationException;
import com.woowacourse.zzimkkong.exception.reservation.ReservationPasswordException;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.infrastructure.TimeConverter;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class GuestReservationService extends ReservationService {
    public GuestReservationService(
            final MapRepository maps,
            final SpaceRepository spaces,
            final ReservationRepository reservations,
            final TimeConverter timeConverter) {
        super(maps, spaces, reservations, timeConverter);
    }

    public ReservationCreateResponse saveReservation(
            final Long mapId,
            final Long spaceId,
            final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
        Map map = maps.findById(mapId).orElseThrow(NoSuchMapException::new);
        validateTime(reservationCreateUpdateWithPasswordRequest);

        Space space = map.getSpaceById(spaceId)
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
            final Long spaceId,
            final Long reservationId,
            final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        Map map = maps.findById(mapId).orElseThrow(NoSuchMapException::new);
        validateSpaceExistence(map, spaceId);

        Reservation reservation = reservations
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);
        checkCorrectPassword(reservation, reservationPasswordAuthenticationRequest.getPassword());
        return ReservationResponse.from(reservation);
    }

    @Transactional(readOnly = true)
    public ReservationFindResponse findReservations(final Long mapId, final Long spaceId, final LocalDate date) {
        Map map = maps.findById(mapId).orElseThrow(NoSuchMapException::new);

        Space space = map.getSpaceById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);
        List<Reservation> reservations = getReservations(Collections.singletonList(space), date);

        return ReservationFindResponse.from(reservations);
    }

    @Transactional(readOnly = true)
    public ReservationFindAllResponse findAllReservations(final Long mapId, final LocalDate date) {
        validateMapExistence(mapId);

        List<Space> findSpaces = spaces.findAllByMapId(mapId);
        List<Reservation> reservations = getReservations(findSpaces, date);

        return ReservationFindAllResponse.of(findSpaces, reservations);
    }

    public void updateReservation(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest) {
        Map map = maps.findById(mapId).orElseThrow(NoSuchMapException::new);
        validateTime(reservationCreateUpdateWithPasswordRequest);

        Space space = map.getSpaceById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);
        Reservation reservation = reservations
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);

        checkCorrectPassword(reservation, reservationCreateUpdateWithPasswordRequest.getPassword());
        validateAvailability(space, reservationCreateUpdateWithPasswordRequest, reservation);

        reservation.update(reservationCreateUpdateWithPasswordRequest, space);
    }

    public void deleteReservation(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        Map map = maps.findById(mapId).orElseThrow(NoSuchMapException::new);
        validateSpaceExistence(map, spaceId);

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
