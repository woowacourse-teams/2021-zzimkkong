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

    private final MapRepository mapRepository;
    private final SpaceRepository spaceRepository;
    private final ReservationRepository reservationRepository;

    public ReservationService(
            final MapRepository mapRepository,
            final SpaceRepository spaceRepository,
            final ReservationRepository reservationRepository) {
        this.mapRepository = mapRepository;
        this.spaceRepository = spaceRepository;
        this.reservationRepository = reservationRepository;
    }

    public ReservationCreateResponse saveReservation(
            final Long mapId,
            final ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
        validateMapExistence(mapId);

        validateTime(reservationCreateUpdateRequest);

        Space space = spaceRepository.findById(reservationCreateUpdateRequest.getSpaceId())
                .orElseThrow(NoSuchSpaceException::new);
        validateAvailability(space, reservationCreateUpdateRequest);

        Reservation reservation = reservationRepository.save(
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

        List<Long> spaceIds = spaceRepository.findAllByMapId(mapId)
                .stream()
                .map(Space::getId)
                .collect(Collectors.toList());

        List<Reservation> reservations = getReservations(spaceIds, date);

        return ReservationFindAllResponse.of(reservations);
    }

    public SlackResponse deleteReservation(
            final Long mapId,
            final Long reservationId,
            final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        validateMapExistence(mapId);
        Reservation reservation = getReservation(reservationId, reservationPasswordAuthenticationRequest.getPassword());
        reservationRepository.delete(reservation);
        return SlackResponse.from(reservation);
    }

    public ReservationResponse findReservation(
            final Long mapId,
            final Long reservationId,
            final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        validateMapExistence(mapId);
        Reservation reservation = getReservation(reservationId, reservationPasswordAuthenticationRequest.getPassword());
        return ReservationResponse.of(reservation);
    }

    public SlackResponse updateReservation(
            final Long mapId,
            final Long reservationId,
            final ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
        validateMapExistence(mapId);
        validateTime(reservationCreateUpdateRequest);

        Space space = spaceRepository.findById(reservationCreateUpdateRequest.getSpaceId())
                .orElseThrow(NoSuchSpaceException::new);
        Reservation reservation = getReservation(reservationId, reservationCreateUpdateRequest.getPassword());
        doDirtyCheck(reservation, reservationCreateUpdateRequest, space);

        validateAvailability(space, reservationCreateUpdateRequest, reservation);

        reservation.update(reservationCreateUpdateRequest, space);
        reservationRepository.save(reservation);
        return SlackResponse.from(reservation);
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

    private Reservation getReservation(final Long reservationId, final String password) {
        Reservation reservation = reservationRepository
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);

        if (reservation.isWrongPassword(password)) {
            throw new ReservationPasswordException();
        }
        return reservation;
    }

    private List<Reservation> getReservations(final Collection<Long> spaceIds, final LocalDate date) {
        LocalDateTime minimumDateTime = date.atStartOfDay();
        LocalDateTime maximumDateTime = minimumDateTime.plusDays(ONE_DAY);

        return reservationRepository.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                spaceIds,
                minimumDateTime,
                maximumDateTime,
                minimumDateTime,
                maximumDateTime
        );
    }

    private void validateMapExistence(final Long mapId) {
        if (!mapRepository.existsById(mapId)) {
            throw new NoSuchMapException();
        }
    }

    private void validateSpaceExistence(final Long spaceId) {
        if (!spaceRepository.existsById(spaceId)) {
            throw new NoSuchSpaceException();
        }
    }
}
