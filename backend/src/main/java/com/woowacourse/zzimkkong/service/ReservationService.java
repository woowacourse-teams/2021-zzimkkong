package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.reservation.*;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils;
import com.woowacourse.zzimkkong.infrastructure.sharingid.SharingIdGenerator;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.service.strategy.ExcludeReservationCreateStrategy;
import com.woowacourse.zzimkkong.service.strategy.ExcludeReservationStrategy;
import com.woowacourse.zzimkkong.service.strategy.ExcludeReservationUpdateStrategy;
import com.woowacourse.zzimkkong.service.strategy.ReservationStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils.*;

@Service
@Transactional
public class ReservationService {
    private final MapRepository maps;
    private final ReservationRepository reservations;
    private final SharingIdGenerator sharingIdGenerator;

    public ReservationService(
            final MapRepository maps,
            final ReservationRepository reservations,
            final SharingIdGenerator sharingIdGenerator) {
        this.maps = maps;
        this.reservations = reservations;
        this.sharingIdGenerator = sharingIdGenerator;
    }

    public ReservationCreateResponse saveReservation(
            final ReservationCreateDto reservationCreateDto,
            final ReservationStrategy reservationStrategy) {
        Long mapId = reservationCreateDto.getMapId();
        String loginEmail = reservationCreateDto.getLoginEmail();

        Map map = maps.findByIdFetch(mapId)
                .orElseThrow(NoSuchMapException::new);
        reservationStrategy.validateManagerOfMap(map, loginEmail);

        Long spaceId = reservationCreateDto.getSpaceId();
        Space space = map.findSpaceById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);

        validateTime(reservationCreateDto, reservationStrategy.isManager());

        validateAvailability(space, reservationCreateDto, new ExcludeReservationCreateStrategy());

        Reservation reservation = reservations.save(
                Reservation.builder()
                        .startTime(reservationCreateDto.getStartDateTime())
                        .endTime(reservationCreateDto.getEndDateTime())
                        .date(reservationCreateDto.getStartDateTime().toLocalDate())
                        .password(reservationCreateDto.getPassword())
                        .userName(reservationCreateDto.getName())
                        .description(reservationCreateDto.getDescription())
                        .space(space)
                        .build());
        String sharingMapId = sharingIdGenerator.from(map);
        return ReservationCreateResponse.of(reservation, sharingMapId, map.getSlackUrl());
    }

    @Transactional(readOnly = true)
    public ReservationFindAllResponse findAllReservations(
            final ReservationFindAllDto reservationFindAllDto,
            final ReservationStrategy reservationStrategy) {
        Long mapId = reservationFindAllDto.getMapId();
        String loginEmail = reservationFindAllDto.getLoginEmail();

        Map map = maps.findByIdFetch(mapId)
                .orElseThrow(NoSuchMapException::new);
        reservationStrategy.validateManagerOfMap(map, loginEmail);

        List<Space> findSpaces = map.getSpaces();
        LocalDate date = reservationFindAllDto.getDate();
        List<Reservation> findReservations = getReservations(findSpaces, date);

        return ReservationFindAllResponse.of(findSpaces, findReservations);
    }

    @Transactional(readOnly = true)
    public ReservationFindResponse findReservations(
            final ReservationFindDto reservationFindDto,
            final ReservationStrategy reservationStrategy) {
        Long mapId = reservationFindDto.getMapId();
        String loginEmail = reservationFindDto.getLoginEmail();

        Map map = maps.findByIdFetch(mapId)
                .orElseThrow(NoSuchMapException::new);
        reservationStrategy.validateManagerOfMap(map, loginEmail);

        Long spaceId = reservationFindDto.getSpaceId();
        LocalDate date = reservationFindDto.getDate();
        Space space = map.findSpaceById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);
        List<Reservation> findReservations = getReservations(Collections.singletonList(space), date);

        return ReservationFindResponse.from(findReservations);
    }

    @Transactional(readOnly = true)
    public ReservationResponse findReservation(
            final ReservationAuthenticationDto reservationAuthenticationDto,
            final ReservationStrategy reservationStrategy) {
        Long mapId = reservationAuthenticationDto.getMapId();
        String loginEmail = reservationAuthenticationDto.getLoginEmail();

        Map map = maps.findByIdFetch(mapId)
                .orElseThrow(NoSuchMapException::new);
        reservationStrategy.validateManagerOfMap(map, loginEmail);

        Long spaceId = reservationAuthenticationDto.getSpaceId();
        validateSpaceExistence(map, spaceId);

        Long reservationId = reservationAuthenticationDto.getReservationId();
        String password = reservationAuthenticationDto.getPassword();
        Reservation reservation = reservations
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);
        reservationStrategy.checkCorrectPassword(reservation, password);

        return ReservationResponse.from(reservation);
    }

    public SlackResponse updateReservation(
            final ReservationUpdateDto reservationUpdateDto,
            final ReservationStrategy reservationStrategy) {
        Long mapId = reservationUpdateDto.getMapId();
        String loginEmail = reservationUpdateDto.getLoginEmail();

        Map map = maps.findByIdFetch(mapId)
                .orElseThrow(NoSuchMapException::new);
        reservationStrategy.validateManagerOfMap(map, loginEmail);

        Long spaceId = reservationUpdateDto.getSpaceId();
        Space space = map.findSpaceById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);

        validateTime(reservationUpdateDto, reservationStrategy.isManager());

        Long reservationId = reservationUpdateDto.getReservationId();
        String password = reservationUpdateDto.getPassword();
        Reservation reservation = reservations
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);
        reservationStrategy.checkCorrectPassword(reservation, password);

        validateAvailability(space, reservationUpdateDto, new ExcludeReservationUpdateStrategy(reservation));

        Reservation updateReservation = Reservation.builder()
                .startTime(reservationUpdateDto.getStartDateTime())
                .endTime(reservationUpdateDto.getEndDateTime())
                .date(reservationUpdateDto.getStartDateTime().toLocalDate())
                .userName(reservationUpdateDto.getName())
                .description(reservationUpdateDto.getDescription())
                .space(space)
                .build();

        reservation.update(updateReservation, space);

        String sharingMapId = sharingIdGenerator.from(map);
        return SlackResponse.of(reservation, sharingMapId, map.getSlackUrl());
    }

    public SlackResponse deleteReservation(
            final ReservationAuthenticationDto reservationAuthenticationDto,
            final ReservationStrategy reservationStrategy) {
        Long mapId = reservationAuthenticationDto.getMapId();
        String loginEmail = reservationAuthenticationDto.getLoginEmail();

        Map map = maps.findByIdFetch(mapId)
                .orElseThrow(NoSuchMapException::new);
        reservationStrategy.validateManagerOfMap(map, loginEmail);

        Long spaceId = reservationAuthenticationDto.getSpaceId();
        validateSpaceExistence(map, spaceId);

        Long reservationId = reservationAuthenticationDto.getReservationId();
        String password = reservationAuthenticationDto.getPassword();
        Reservation reservation = reservations
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);
        reservationStrategy.checkCorrectPassword(reservation, password);
        validatePastTimeAndManager(reservation.getStartTime(), reservationStrategy.isManager());

        reservations.delete(reservation);

        String sharingMapId = sharingIdGenerator.from(map);
        return SlackResponse.of(reservation, sharingMapId, map.getSlackUrl());
    }

    private void validateTime(final ReservationCreateDto reservationCreateDto, final boolean managerFlag) {
        LocalDateTime startDateTime = reservationCreateDto.getStartDateTime().withSecond(0).withNano(0);
        LocalDateTime endDateTime = reservationCreateDto.getEndDateTime().withSecond(0).withNano(0);

        validatePastTimeAndManager(startDateTime, managerFlag);

        if (endDateTime.isBefore(startDateTime) || startDateTime.equals(endDateTime)) {
            throw new ImpossibleEndTimeException();
        }

        LocalDate startDateKST = TimeZoneUtils.convert(startDateTime, UTC, KST).toLocalDate();
        LocalDate endDateKST = TimeZoneUtils.convert(endDateTime, UTC, KST).toLocalDate();
        if (!startDateKST.isEqual(endDateKST)) {
            throw new NonMatchingStartAndEndDateException();
        }
    }

    private void validatePastTimeAndManager(final LocalDateTime startDateTime, final boolean managerFlag) {
        if (startDateTime.isBefore(LocalDateTime.now()) && !managerFlag) {
            throw new ImpossibleStartTimeException();
        }
    }

    private void validateAvailability(
            final Space space,
            final ReservationCreateDto reservationCreateDto,
            final ExcludeReservationStrategy excludeReservationStrategy) {
        LocalDateTime startDateTime = reservationCreateDto.getStartDateTime();
        LocalDateTime endDateTime = reservationCreateDto.getEndDateTime();

        validateSpaceSetting(space, startDateTime, endDateTime);

        List<Reservation> reservationsOnDate = getReservations(
                Collections.singletonList(space),
                startDateTime.toLocalDate());
        excludeReservationStrategy.apply(space, reservationsOnDate);

        validateTimeConflicts(startDateTime, endDateTime, reservationsOnDate);
    }

    private void validateSpaceSetting(
            final Space space,
            final LocalDateTime startDateTime,
            final LocalDateTime endDateTime) {
        LocalDateTime startDateTimeKST = TimeZoneUtils.convert(startDateTime, UTC, KST);
        LocalDateTime endDateTimeKST = TimeZoneUtils.convert(endDateTime, UTC, KST);
        int durationMinutes = (int) ChronoUnit.MINUTES.between(startDateTimeKST, endDateTimeKST);

        if (space.isNotDivisibleByTimeUnit(startDateTimeKST.getMinute()) || space.isNotDivisibleByTimeUnit(durationMinutes)) {
            throw new InvalidTimeUnitException();
        }

        if (space.isIncorrectMinimumTimeUnit(durationMinutes)) {
            throw new InvalidMinimumDurationTimeException();
        }

        if (space.isIncorrectMaximumTimeUnit(durationMinutes)) {
            throw new InvalidMaximumDurationTimeException();
        }

        if (space.isNotBetweenAvailableTime(startDateTimeKST, endDateTimeKST)) {
            throw new InvalidStartEndTimeException();
        }

        if (space.isUnableToReserve()) {
            throw new InvalidReservationEnableException();
        }

        if (space.isClosedOn(startDateTimeKST.getDayOfWeek())) {
            throw new InvalidDayOfWeekException();
        }
    }

    private void validateTimeConflicts(
            final LocalDateTime startDateTime,
            final LocalDateTime endDateTime,
            final List<Reservation> reservationsOnDate) {
        for (Reservation existingReservation : reservationsOnDate) {
            if (existingReservation.hasConflictWith(startDateTime, endDateTime)) {
                throw new ReservationAlreadyExistsException();
            }
        }
    }

    private List<Reservation> getReservations(final Collection<Space> findSpaces, final LocalDate date) {
        List<Long> spaceIds = findSpaces.stream()
                .map(Space::getId)
                .collect(Collectors.toList());

        List<Reservation> reservationsWithinDateRange = reservations.findAllBySpaceIdInAndDateGreaterThanEqualAndDateLessThanEqual(
                spaceIds,
                date.minusDays(ONE_DAY_OFFSET),
                date.plusDays(ONE_DAY_OFFSET));

        return reservationsWithinDateRange.stream()
                .filter(reservation -> reservation.isBookedOn(date, KST))
                .collect(Collectors.toList());
    }

    private void validateSpaceExistence(final Map map, final Long spaceId) {
        if (map.doesNotHaveSpaceId(spaceId)) {
            throw new NoSuchSpaceException();
        }
    }
}
