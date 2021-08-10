package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateDto;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateResponse;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateUpdateRequest;
import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateUpdateWithPasswordRequest;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.reservation.*;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.infrastructure.ReservationCallback;
import com.woowacourse.zzimkkong.infrastructure.TimeConverter;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservationService2 {
    private static final long ONE_DAY = 1L;

    protected MapRepository maps;
    protected SpaceRepository spaces;
    protected ReservationRepository reservations;
    protected TimeConverter timeConverter;

    public ReservationService2(
            final MapRepository maps,
            final SpaceRepository spaces,
            final ReservationRepository reservations,
            final TimeConverter timeConverter) {
        this.maps = maps;
        this.spaces = spaces;
        this.reservations = reservations;
        this.timeConverter = timeConverter;
    }

    public ReservationCreateResponse saveReservation(final ReservationCreateDto reservationCreateDto, final ReservationCallback reservationCallback) {
        Long mapId = reservationCreateDto.getMapId();
        Member manager = reservationCreateDto.getManager();
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        reservationCallback.validateManagerOfMap(map, manager);

        Long spaceId = reservationCreateDto.getSpaceId();
        Space space = map.findSpaceById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);

        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = reservationCreateDto.getRequest();
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

    private void validateTime(final ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
        LocalDateTime startDateTime = reservationCreateUpdateRequest.getStartDateTime().withSecond(0).withNano(0);
        LocalDateTime endDateTime = reservationCreateUpdateRequest.getEndDateTime().withSecond(0).withNano(0);

        if (startDateTime.isBefore(timeConverter.getNow())) {
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

        validateSpaceSetting(space, startDateTime, endDateTime);

        List<Reservation> reservationsOnDate = getReservations(
                Collections.singletonList(space),
                startDateTime.toLocalDate());

        validateTimeConflicts(startDateTime, endDateTime, reservationsOnDate);
    }

    private void validateSpaceSetting(Space space, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        int durationMinutes = (int) ChronoUnit.MINUTES.between(startDateTime, endDateTime);

        if (space.isIncorrectTimeUnit(startDateTime.getMinute()) | space.isNotDivideBy(durationMinutes)) {
            throw new InvalidTimeUnitException();
        }

        if (space.isIncorrectMinimumMaximumTimeUnit(durationMinutes)) {
            throw new InvalidDurationTimeException();
        }

        if (space.isNotBetweenAvailableTime(startDateTime, endDateTime)) {
            throw new InvalidStartEndTimeException();
        }

        if (space.isUnableToReserve()) {
            throw new InvalidReservationEnableException();
        }

        if (space.isClosedOn(startDateTime.getDayOfWeek())) {
            throw new InvalidDayOfWeekException();
        }
    }

    private void excludeTargetReservation(final Space space, final Reservation reservation, final List<Reservation> reservationsOnDate) {
        if (reservation.getSpace().equals(space)) {
            reservationsOnDate.remove(reservation);
        }
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

    protected List<Reservation> getReservations(final Collection<Space> findSpaces, final LocalDate date) {
        LocalDateTime minimumDateTime = date.atStartOfDay();
        LocalDateTime maximumDateTime = minimumDateTime.plusDays(ONE_DAY);
        List<Long> spaceIds = findSpaces.stream()
                .map(Space::getId)
                .collect(Collectors.toList());

        return reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                spaceIds,
                minimumDateTime,
                maximumDateTime,
                minimumDateTime,
                maximumDateTime
        );
    }

    protected void validateSpaceExistence(final Map map, final Long spaceId) {
        if (map.doesNotHaveSpaceId(spaceId)) {
            throw new NoSuchSpaceException();
        }
    }
}
