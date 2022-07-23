package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.dto.slack.SlackResponse;
import com.woowacourse.zzimkkong.exception.setting.MultipleSettingsException;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.reservation.*;
import com.woowacourse.zzimkkong.exception.setting.NoSettingAvailableException;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
                        .reservationTime(reservationTime)
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
                .reservationTime(reservationTime)
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

        if (!reservationStrategy.isManager()) {
            ReservationTime.validatePastTime(reservation.getStartTime());
        }

        reservations.delete(reservation);

        String sharingMapId = sharingIdGenerator.from(map);
        return SlackResponse.of(reservation, sharingMapId, map.getSlackUrl());
    }

    private void validateAvailability(
            final Space space,
            final ReservationTime reservationTime,
            final ExcludeReservationStrategy excludeReservationStrategy) {
        validateSpaceSetting(space, reservationTime);

        List<Reservation> reservationsOnDate = getReservations(
                Collections.singletonList(space),
                reservationTime.getDate());
        excludeReservationStrategy.apply(space, reservationsOnDate);

        validateTimeConflicts(reservationTime, reservationsOnDate);
    }

    private void validateSpaceSetting(final Space space, final ReservationTime reservationTime) {
        TimeSlot timeSlot = reservationTime.asTimeSlotKST();
        DayOfWeek dayOfWeek = reservationTime.getDayOfWeekKST();

        Settings relevantSettings = space.getRelevantSettings(timeSlot, dayOfWeek);
        if (relevantSettings.isEmpty()) {
            throw new NoSettingAvailableException(space);
        }

        // TODO: 추후 N부제 -> 예약 유도로 넘어갈 때 이부분이 제거되어야함 - 여러 조건에 걸치면 유도하는 식으로 로직이 변경되어야 하기 때문
        // TODO: exception message test?
        if (relevantSettings.haveMultipleSettings()) {
            throw new MultipleSettingsException(relevantSettings);
        }

        if (relevantSettings.cannotAcceptDueToAvailableTime(timeSlot)) {
            throw new InvalidStartEndTimeException(relevantSettings, timeSlot);
        }

        Setting setting = relevantSettings.getSettings().get(0);

        if (setting.cannotAcceptDueToTimeUnit(timeSlot)) {
            throw new InvalidTimeUnitException();
        }

        if (setting.cannotAcceptDueToMinimumTimeUnit(timeSlot)) {
            throw new InvalidMinimumDurationTimeException();
        }

        if (setting.cannotAcceptDueToMaximumTimeUnit(timeSlot)) {
            throw new InvalidMaximumDurationTimeException();
        }

        if (space.isUnableToReserve()) {
            throw new InvalidReservationEnableException();
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
        List<Long> spaceIds = findSpaces.stream()
                .map(Space::getId)
                .collect(Collectors.toList());

        return reservations.findAllBySpaceIdInAndReservationTimeDate(spaceIds, date);
    }

    private void validateSpaceExistence(final Map map, final Long spaceId) {
        if (map.doesNotHaveSpaceId(spaceId)) {
            throw new NoSuchSpaceException();
        }
    }
}
