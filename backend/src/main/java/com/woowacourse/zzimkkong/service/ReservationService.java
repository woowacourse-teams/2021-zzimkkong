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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public abstract class ReservationService {
    public static final long ONE_DAY = 1L;

    protected MapRepository maps;
    protected SpaceRepository spaces;
    protected ReservationRepository reservations;

    protected ReservationService(
            final MapRepository maps,
            final SpaceRepository spaces,
            final ReservationRepository reservations) {
        this.maps = maps;
        this.spaces = spaces;
        this.reservations = reservations;
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

        return ReservationCreateResponse.of(reservation);
    }

    @Transactional(readOnly = true)
    public ReservationFindResponse findReservations(final Long mapId, final Long spaceId, final LocalDate date) {
        validateMapExistence(mapId);
        validateSpaceExistence(spaceId);

        List<Reservation> reservations = getReservations(Collections.singletonList(spaceId), date);

        return ReservationFindResponse.of(reservations);
    }

    @Transactional(readOnly = true)
    public ReservationFindAllResponse findAllReservations(final Long mapId, final LocalDate date) {
        validateMapExistence(mapId);

        List<Long> spaceIds = spaces.findAllByMapId(mapId)
                .stream()
                .map(Space::getId)
                .collect(Collectors.toList());

        List<Reservation> reservations = getReservations(spaceIds, date);

        return ReservationFindAllResponse.of(reservations);
    }

    @Transactional(readOnly = true)
    public abstract ReservationResponse findReservation(
            final Long mapId,
            final Long reservationId,
            final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest);

    public abstract SlackResponse deleteReservation(
            final Long mapId,
            final Long reservationId,
            final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest);

    public abstract SlackResponse updateReservation(
            final Long mapId,
            final Long reservationId,
            final ReservationCreateUpdateRequest reservationCreateUpdateRequest);

    protected void validateTime(final ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
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

    protected void validateAvailability(
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

    protected void doDirtyCheck(
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

    protected void checkCorrectPassword(final Reservation reservation, final String password) {
        if (reservation.isWrongPassword(password)) {
            throw new ReservationPasswordException();
        }
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

    protected void validateMapExistence(final Long mapId) {
        if (!maps.existsById(mapId)) {
            throw new NoSuchMapException();
        }
    }

    private void validateSpaceExistence(final Long spaceId) {
        if (!spaces.existsById(spaceId)) {
            throw new NoSuchSpaceException();
        }
    }
}
