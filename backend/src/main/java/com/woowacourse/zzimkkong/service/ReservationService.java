package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.reservation.*;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.infrastructure.sharingid.SharingIdGenerator;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.service.strategy.ExcludeReservationCreateStrategy;
import com.woowacourse.zzimkkong.service.strategy.ExcludeReservationStrategy;
import com.woowacourse.zzimkkong.service.strategy.ExcludeReservationUpdateStrategy;
import com.woowacourse.zzimkkong.service.strategy.ReservationStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

        ReservationTime reservationTime = ReservationTime.of(
                reservationCreateDto.getStartDateTime(),
                reservationCreateDto.getEndDateTime(),
                reservationStrategy.isManager());

        validateAvailability(space, reservationTime, new ExcludeReservationCreateStrategy());

        Reservation reservation = reservations.save(
                Reservation.builder()
                        .startTime(reservationTime.getStartDateTime())
                        .endTime(reservationTime.getEndDateTime())
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

        ReservationTime reservationTime = ReservationTime.of(
                reservationUpdateDto.getStartDateTime(),
                reservationUpdateDto.getEndDateTime(),
                reservationStrategy.isManager());

        Long reservationId = reservationUpdateDto.getReservationId();
        String password = reservationUpdateDto.getPassword();
        Reservation reservation = reservations
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);
        reservationStrategy.checkCorrectPassword(reservation, password);

        validateAvailability(space, reservationTime, new ExcludeReservationUpdateStrategy(reservation));

        Reservation updateReservation = Reservation.builder()
                .startTime(reservationTime.getStartDateTime())
                .endTime(reservationTime.getEndDateTime())
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

    private void validatePastTimeAndManager(final LocalDateTime startDateTime, final boolean managerFlag) {
        if (startDateTime.isBefore(LocalDateTime.now()) && !managerFlag) {
            throw new ImpossibleStartTimeException();
        }
    }

    private void validateAvailability(
            final Space space,
            final ReservationTime reservationTime,
            final ExcludeReservationStrategy excludeReservationStrategy) {
        validateSpaceSetting(space, reservationTime);

        List<Reservation> reservationsOnDate = getReservations(
                Collections.singletonList(space),
                reservationTime.getDateKST());
        excludeReservationStrategy.apply(space, reservationsOnDate);

        validateTimeConflicts(reservationTime, reservationsOnDate);
    }

    private void validateSpaceSetting(final Space space, final ReservationTime reservationTime) {
        TimeSlot timeSlot = reservationTime.getTimeSlotKST();
        DayOfWeek dayOfWeek = reservationTime.getDayOfWeekKST();

        if (space.cannotAcceptDueToTimeUnit(timeSlot)) {
            throw new InvalidTimeUnitException();
        }

        if (space.cannotAcceptDueToMinimumTimeUnit(timeSlot)) {
            throw new InvalidMinimumDurationTimeException();
        }

        if (space.cannotAcceptDueToMaximumTimeUnit(timeSlot)) {
            throw new InvalidMaximumDurationTimeException();
        }

        if (space.cannotAcceptDueToAvailableTime(timeSlot)) {
            throw new InvalidStartEndTimeException();
        }

        if (space.isUnableToReserve()) {
            throw new InvalidReservationEnableException();
        }

        if (space.isClosedOn(dayOfWeek)) {
            throw new InvalidDayOfWeekException();
        }
    }

    private void validateTimeConflicts(
            final ReservationTime reservationTime,
            final List<Reservation> reservationsOnDate) {
        for (Reservation existingReservation : reservationsOnDate) {
            if (existingReservation.hasConflictWith(reservationTime)) {
                throw new ReservationAlreadyExistsException();
            }
        }
    }

    private List<Reservation> getReservations(final Collection<Space> findSpaces, final LocalDate date) {
        //TODO: Reservations
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
