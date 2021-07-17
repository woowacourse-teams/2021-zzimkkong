package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.reservation.*;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservationService {
    public static final long ONE_DAY = 1L;

    private final MapRepository maps;
    private final SpaceRepository spaces;
    private final ReservationRepository reservations;

    public ReservationService(
            final MapRepository mapRepository,
            final SpaceRepository spaceRepository,
            final ReservationRepository reservationRepository) {
        this.maps = mapRepository;
        this.spaces = spaceRepository;
        this.reservations = reservationRepository;
    }

    public ReservationCreateResponse saveReservation(
            final Long mapId,
            final ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
        validateMapExistence(mapId);

        validateTime(reservationCreateUpdateRequest);
        Space space = spaces.findById(reservationCreateUpdateRequest.getSpaceId())
                .orElseThrow(NoSuchSpaceException::new);
        validateAvailability(space, reservationCreateUpdateRequest);

        Reservation reservation = reservations.save(
                new Reservation.Builder()
                        .startTime(reservationCreateUpdateRequest.getStartDateTime())
                        .endTime(reservationCreateUpdateRequest.getEndDateTime())
                        .password(reservationCreateUpdateRequest.getPassword())
                        .userName(reservationCreateUpdateRequest.getName())
                        .description(reservationCreateUpdateRequest.getDescription())
                        .space(space)
                        .build());

        return ReservationCreateResponse.from(reservation);
    }

    @Transactional(readOnly = true)
    public ReservationFindResponse findReservations(final Long mapId, final Long spaceId, final LocalDate date) {
        validateMapExistence(mapId);
        validateSpaceExistence(spaceId);

        List<Reservation> reservations = getReservations(Collections.singletonList(spaceId), date);

        return ReservationFindResponse.from(reservations);
    }

    @Transactional(readOnly = true)
    public ReservationFindAllResponse findAllReservations(final Long mapId, final LocalDate date) {
        validateMapExistence(mapId);

        List<Long> spaceIds = spaces.findAllByMapId(mapId)
                .stream()
                .map(Space::getId)
                .collect(Collectors.toList());

        List<Reservation> reservations = getReservations(spaceIds, date);

        return ReservationFindAllResponse.from(reservations);
    }

    @Transactional(readOnly = true)
    public ReservationResponse findReservation(
            final Long mapId,
            final Long reservationId,
            final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        validateMapExistence(mapId);

        Reservation reservation = getReservation(reservationId);
        checkCorrectPassword(reservation, reservationPasswordAuthenticationRequest.getPassword());
        return ReservationResponse.from(reservation);
    }

    public SlackResponse updateReservation(
            final Long mapId,
            final Long reservationId,
            final ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
        validateMapExistence(mapId);
        validateTime(reservationCreateUpdateRequest);

        Space space = spaces.findById(reservationCreateUpdateRequest.getSpaceId())
                .orElseThrow(NoSuchSpaceException::new);
        Reservation reservation = getReservation(reservationId);

        checkCorrectPassword(reservation, reservationCreateUpdateRequest.getPassword());
        doDirtyCheck(reservation, reservationCreateUpdateRequest, space);
        validateAvailability(space, reservationCreateUpdateRequest, reservation);

        reservation.update(reservationCreateUpdateRequest, space);
        reservations.save(reservation);
        return SlackResponse.from(reservation);
    }

    public SlackResponse deleteReservation(
            final Long mapId,
            final Long reservationId,
            final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        validateMapExistence(mapId);

        Reservation reservation = getReservation(reservationId);
        checkCorrectPassword(reservation, reservationPasswordAuthenticationRequest.getPassword());
        reservations.delete(reservation);
        return SlackResponse.from(reservation);
    }

    private void validateTime(final ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
        LocalDateTime startDateTime = reservationCreateUpdateRequest.getStartDateTime();
        LocalDateTime endDateTime = reservationCreateUpdateRequest.getEndDateTime();

        if (startDateTime.isBefore(LocalDateTime.now())) {
            throw new ImpossibleStartTimeException();
        }

        if (endDateTime.isBefore(startDateTime) || startDateTime.equals(endDateTime)) {
            throw new ImpossibleEndTimeException();
        }

        if (!startDateTime.toLocalDate().isEqual(endDateTime.toLocalDate())) {
            throw new NonMatchingStartAndEndDateException();
        }
    }

    private void validateAvailability(
            final Space space,
            final ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
        LocalDateTime startDateTime = reservationCreateUpdateRequest.getStartDateTime();
        LocalDateTime endDateTime = reservationCreateUpdateRequest.getEndDateTime();

        List<Reservation> reservationsOnDate = getReservations(
                Collections.singletonList(space.getId()),
                startDateTime.toLocalDate());

        validateTimeConflicts(startDateTime, endDateTime, reservationsOnDate);
    }

    private void validateAvailability(
            final Space space,
            final ReservationCreateUpdateRequest reservationCreateUpdateRequest,
            final Reservation reservation) {
        LocalDateTime startDateTime = reservationCreateUpdateRequest.getStartDateTime();
        LocalDateTime endDateTime = reservationCreateUpdateRequest.getEndDateTime();

        List<Reservation> reservationsOnDate = getReservations(
                Collections.singletonList(space.getId()),
                startDateTime.toLocalDate());

        if (reservation.getSpace().equals(space)) {
            reservationsOnDate.remove(reservation);
        }

        validateTimeConflicts(startDateTime, endDateTime, reservationsOnDate);
    }

    private void validateTimeConflicts(
            final LocalDateTime startDateTime,
            final LocalDateTime endDateTime,
            final List<Reservation> reservationsOnDate) {
        for (Reservation existingReservation : reservationsOnDate) {
            if (existingReservation.hasConflictWith(startDateTime, endDateTime)) {
                throw new ImpossibleReservationTimeException();
            }
        }
    }

    private void validateMapExistence(final Long mapId) {
        if (!maps.existsById(mapId)) {
            throw new NoSuchMapException();
        }
    }

    private void validateSpaceExistence(final Long spaceId) {
        if (!spaces.existsById(spaceId)) {
            throw new NoSuchSpaceException();
        }
    }

    private void checkCorrectPassword(final Reservation reservation, final String password) {
        if (reservation.isWrongPassword(password)) {
            throw new ReservationPasswordException();
        }
    }

    private Reservation getReservation(final Long reservationId) {
        return reservations
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);
    }

    private List<Reservation> getReservations(final Collection<Long> spaceIds, final LocalDate date) {
        LocalDateTime minimumDateTime = date.atStartOfDay();
        LocalDateTime maximumDateTime = minimumDateTime.plusDays(ONE_DAY);

        return reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                spaceIds,
                minimumDateTime,
                maximumDateTime,
                minimumDateTime,
                maximumDateTime
        );
    }

    private void doDirtyCheck(
            final Reservation reservation,
            final ReservationCreateUpdateRequest reservationCreateUpdateRequest,
            final Space space) {
        Reservation updatedReservation = new Reservation.Builder()
                .startTime(reservationCreateUpdateRequest.getStartDateTime())
                .endTime(reservationCreateUpdateRequest.getEndDateTime())
                .userName(reservationCreateUpdateRequest.getName())
                .description(reservationCreateUpdateRequest.getDescription())
                .space(space)
                .build();

        if (reservation.hasSameData(updatedReservation)) {
            throw new NoDataToUpdateException();
        }
    }
}
