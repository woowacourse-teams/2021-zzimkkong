package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.reservation.NoSuchReservationException;
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
public class ManagerReservationService extends ReservationService {
    public ManagerReservationService(
            final MapRepository maps,
            final SpaceRepository spaces,
            final ReservationRepository reservations,
            final TimeConverter timeConverter) {
        super(maps, spaces, reservations, timeConverter);
    }

    public ReservationCreateResponse saveReservation(
            final Long mapId,
            final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest,
            final Member manager) {
        validateAuthorityOnMap(mapId, manager);
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
    public ReservationFindAllResponse findAllReservations(
            final Long mapId,
            final LocalDate date,
            final Member manager) {
        validateAuthorityOnMap(mapId, manager);

        List<Space> findSpaces = spaces.findAllByMapId(mapId);
        List<Reservation> reservations = getReservations(findSpaces, date);

        return ReservationFindAllResponse.of(findSpaces, reservations);
    }

    @Transactional(readOnly = true)
    public ReservationFindResponse findReservations(
            final Long mapId,
            final Long spaceId,
            final LocalDate date,
            final Member manager) {
        validateAuthorityOnMap(mapId, manager);

        Space space = spaces.findById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);
        List<Reservation> reservations = getReservations(Collections.singletonList(space), date);

        return ReservationFindResponse.from(reservations);
    }

    @Transactional(readOnly = true)
    public ReservationResponse findReservation(
            final Long mapId,
            final Long reservationId,
            final Member manager) {
        validateAuthorityOnMap(mapId, manager);

        Reservation reservation = reservations
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);
        return ReservationResponse.from(reservation);
    }

    public SlackResponse updateReservation(
            final Long mapId,
            final Long reservationId,
            final ReservationCreateUpdateRequest reservationCreateUpdateRequest,
            final Member manager) {
        validateAuthorityOnMap(mapId, manager);
        validateTime(reservationCreateUpdateRequest);

        Space space = spaces.findById(reservationCreateUpdateRequest.getSpaceId())
                .orElseThrow(NoSuchSpaceException::new);
        Reservation reservation = reservations
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);

        validateAvailability(space, reservationCreateUpdateRequest, reservation);

        reservation.update(reservationCreateUpdateRequest, space);
        return SlackResponse.from(reservation);
    }

    public SlackResponse deleteReservation(
            final Long mapId,
            final Long reservationId,
            final Member manager) {
        validateAuthorityOnMap(mapId, manager);

        Reservation reservation = reservations
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);
        reservations.delete(reservation);
        return SlackResponse.from(reservation);
    }

    private void validateAuthorityOnMap(final Long mapId, final Member manager) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(manager, map);
    }

    private void validateManagerOfMap(Member manager, Map map) {
        if (map.isNotOwnedBy(manager)) {
            throw new NoAuthorityOnMapException();
        }
    }
}
