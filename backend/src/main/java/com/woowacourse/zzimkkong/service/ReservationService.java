package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.reservation.*;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.infrastructure.TimeConverter;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.service.callback.ReservationStrategy;
import com.woowacourse.zzimkkong.service.callback.ExcludeReservationCreateStrategy;
import com.woowacourse.zzimkkong.service.callback.ExcludeReservationStrategy;
import com.woowacourse.zzimkkong.service.callback.ExcludeReservationUpdateStrategy;
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
public class ReservationService {
    private static final long ONE_DAY = 1L;

    private final MapRepository maps;
    private final ReservationRepository reservations;
    private final TimeConverter timeConverter;

    public ReservationService(
            final MapRepository maps,
            final ReservationRepository reservations,
            final TimeConverter timeConverter) {
        this.maps = maps;
        this.reservations = reservations;
        this.timeConverter = timeConverter;
    }

    public ReservationCreateResponse saveReservation(
            final ReservationCreateDto reservationCreateDto,
            final ReservationStrategy reservationStrategy) {
        Long mapId = reservationCreateDto.getMapId();
        Member manager = reservationCreateDto.getManager();
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        reservationStrategy.validateManagerOfMap(map, manager);

        Long spaceId = reservationCreateDto.getSpaceId();
        Space space = map.findSpaceById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);

        validateTime(reservationCreateDto);

        validateAvailability(space, reservationCreateDto, new ExcludeReservationCreateStrategy());

        Reservation reservation = reservations.save(
                Reservation.builder()
                        .startTime(reservationCreateDto.getStartDateTime())
                        .endTime(reservationCreateDto.getEndDateTime())
                        .password(reservationCreateDto.getPassword())
                        .userName(reservationCreateDto.getName())
                        .description(reservationCreateDto.getDescription())
                        .space(space)
                        .build());

        return ReservationCreateResponse.from(reservation);
    }

    @Transactional(readOnly = true)
    public ReservationFindAllResponse findAllReservations(
            final ReservationFindAllDto reservationFindAllDto,
            final ReservationStrategy reservationStrategy) {
        Long mapId = reservationFindAllDto.getMapId();
        Member manager = reservationFindAllDto.getManager();
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        reservationStrategy.validateManagerOfMap(map, manager);

        List<Space> findSpaces = map.getSpaces();
        LocalDate date = reservationFindAllDto.getDate();
        List<Reservation> reservations = getReservations(findSpaces, date);

        return ReservationFindAllResponse.of(findSpaces, reservations);
    }

    @Transactional(readOnly = true)
    public ReservationFindResponse findReservations(
            final ReservationFindDto reservationFindDto,
            final ReservationStrategy reservationStrategy) {
        Long mapId = reservationFindDto.getMapId();
        Member manager = reservationFindDto.getManager();
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        reservationStrategy.validateManagerOfMap(map, manager);

        Long spaceId = reservationFindDto.getSpaceId();
        LocalDate date = reservationFindDto.getDate();
        Space space = map.findSpaceById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);
        List<Reservation> reservations = getReservations(Collections.singletonList(space), date);

        return ReservationFindResponse.from(reservations);
    }

    @Transactional(readOnly = true)
    public ReservationResponse findReservation(
            final ReservationAuthenticationDto reservationAuthenticationDto,
            final ReservationStrategy reservationStrategy) {
        Long mapId = reservationAuthenticationDto.getMapId();
        Member manager = reservationAuthenticationDto.getManager();
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        reservationStrategy.validateManagerOfMap(map, manager);

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
        Member manager = reservationUpdateDto.getManager();
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        reservationStrategy.validateManagerOfMap(map, manager);

        Long spaceId = reservationUpdateDto.getSpaceId();
        Space space = map.findSpaceById(spaceId)
                .orElseThrow(NoSuchSpaceException::new);

        validateTime(reservationUpdateDto);

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
                .userName(reservationUpdateDto.getName())
                .description(reservationUpdateDto.getDescription())
                .space(space)
                .build();

        reservation.update(updateReservation, space);

        return reservationStrategy.createSlackResponse(reservation);
    }

    public SlackResponse deleteReservation(
            final ReservationAuthenticationDto reservationAuthenticationDto,
            final ReservationStrategy reservationStrategy) {
        Long mapId = reservationAuthenticationDto.getMapId();
        Member manager = reservationAuthenticationDto.getManager();
        Map map = maps.findById(mapId)
                .orElseThrow(NoSuchMapException::new);
        reservationStrategy.validateManagerOfMap(map, manager);

        Long spaceId = reservationAuthenticationDto.getSpaceId();
        validateSpaceExistence(map, spaceId);

        Long reservationId = reservationAuthenticationDto.getReservationId();
        String password = reservationAuthenticationDto.getPassword();
        Reservation reservation = reservations
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);
        reservationStrategy.checkCorrectPassword(reservation, password);

        reservations.delete(reservation);
        return reservationStrategy.createSlackResponse(reservation);
    }

    private void validateTime(final ReservationCreateDto reservationCreateDto) {
        LocalDateTime startDateTime = reservationCreateDto.getStartDateTime().withSecond(0).withNano(0);
        LocalDateTime endDateTime = reservationCreateDto.getEndDateTime().withSecond(0).withNano(0);

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

    private List<Reservation> getReservations(final Collection<Space> findSpaces, final LocalDate date) {
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

    private void validateSpaceExistence(final Map map, final Long spaceId) {
        if (map.doesNotHaveSpaceId(spaceId)) {
            throw new NoSuchSpaceException();
        }
    }
}
