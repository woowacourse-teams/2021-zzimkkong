package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateUpdateRequest;
import com.woowacourse.zzimkkong.dto.reservation.ReservationFindAllResponse;
import com.woowacourse.zzimkkong.dto.reservation.ReservationFindResponse;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.reservation.*;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ReservationService {
    private static final long ONE_DAY = 1L;

    protected MapRepository maps;
    protected SpaceRepository spaces;
    protected ReservationRepository reservations;

    protected ReservationService(MapRepository maps, SpaceRepository spaces, ReservationRepository reservations) {
        this.maps = maps;
        this.spaces = spaces;
        this.reservations = reservations;
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

    protected void validateTime(final ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
        LocalDateTime startDateTime = reservationCreateUpdateRequest.getStartDateTime();
        LocalDateTime endDateTime = reservationCreateUpdateRequest.getEndDateTime();

        validateStartTimeInPast(startDateTime);

        if (endDateTime.isBefore(startDateTime) || startDateTime.equals(endDateTime)) {
            throw new ImpossibleEndTimeException();
        }

        if (!startDateTime.toLocalDate().isEqual(endDateTime.toLocalDate())) {
            throw new NonMatchingStartAndEndDateException();
        }
    }

    private void validateStartTimeInPast(final LocalDateTime startDateTime) {
        LocalDateTime localNow = LocalDateTime.now();
        ZonedDateTime zonedLocal = localNow.atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"));
        ZonedDateTime zonedLocal = localNow.atZone(ZoneId.of("Asia/Seoul"));
      
        ZonedDateTime zonedStartDateTime = startDateTime.atZone(ZoneId.of("Asia/Seoul"));

        if (zonedStartDateTime.isBefore(zonedLocal)) {
            throw new ImpossibleStartTimeException();
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

        excludeTargetReservation(space, reservation, reservationsOnDate);

        validateTimeConflicts(startDateTime, endDateTime, reservationsOnDate);
    }

    private void excludeTargetReservation(final Space space, final Reservation reservation, final List<Reservation> reservationsOnDate) {
        if (reservation.getSpace().equals(space)) {
            reservationsOnDate.remove(reservation);
        }
    }

    protected void validateAvailability(
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
