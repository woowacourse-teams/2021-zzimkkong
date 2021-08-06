package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
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

import static com.woowacourse.zzimkkong.service.MapService.validateManagerOfMap;

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
            final Long spaceId,
            final ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest,
            final Member manager) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(map, manager);

        Space space = map.getSpaceById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);
        validateTime(reservationCreateUpdateWithPasswordRequest);
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
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(map, manager);

        List<Space> findSpaces = map.getSpaces();
        List<Reservation> reservations = getReservations(findSpaces, date);

        return ReservationFindAllResponse.of(findSpaces, reservations);
    }

    @Transactional(readOnly = true)
    public ReservationFindResponse findReservations(
            final Long mapId,
            final Long spaceId,
            final LocalDate date,
            final Member manager) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(map, manager);

        Space space = map.getSpaceById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);
        List<Reservation> reservations = getReservations(Collections.singletonList(space), date);

        return ReservationFindResponse.from(reservations);
    }

    @Transactional(readOnly = true)
    public ReservationResponse findReservation(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final Member manager) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(map, manager);

        validateSpaceExistence(map, spaceId);

        Reservation reservation = reservations
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);
        return ReservationResponse.from(reservation);
    }

    public SlackResponse updateReservation(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final ReservationCreateUpdateRequest reservationCreateUpdateRequest,
            final Member manager) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(map, manager);

        Space space = map.getSpaceById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);

        validateTime(reservationCreateUpdateRequest);
        Reservation reservation = reservations
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);

        validateAvailability(space, reservationCreateUpdateRequest, reservation);

        Reservation updateReservation = new Reservation.Builder()
                .startTime(reservationCreateUpdateRequest.getStartDateTime())
                .endTime(reservationCreateUpdateRequest.getEndDateTime())
                .userName(reservationCreateUpdateRequest.getName())
                .description(reservationCreateUpdateRequest.getDescription())
                .space(space)
                .build();

        reservation.update(updateReservation, space);
        return SlackResponse.from(reservation);
    }

    public SlackResponse deleteReservation(
            final Long mapId,
            final Long spaceId,
            final Long reservationId,
            final Member manager) {
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        validateManagerOfMap(map, manager);

        validateSpaceExistence(map, spaceId);

        Reservation reservation = reservations
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);
        reservations.delete(reservation);
        return SlackResponse.from(reservation);
    }
}
