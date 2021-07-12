package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.*;
import com.woowacourse.zzimkkong.exception.*;
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

    public ReservationCreateResponse saveReservation(Long mapId, ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
        validateMapExistence(mapId);

        LocalDateTime startDateTime = reservationCreateUpdateRequest.getStartDateTime();
        LocalDateTime endDateTime = reservationCreateUpdateRequest.getEndDateTime();

        validateTime(startDateTime, endDateTime);

        Space space = spaceRepository.findById(reservationCreateUpdateRequest.getSpaceId())
                .orElseThrow(NoSuchSpaceException::new);
        List<Reservation> reservations = getReservations(
                Collections.singletonList(space.getId()),
                startDateTime.toLocalDate());
        validateAvailability(reservations, startDateTime, endDateTime);

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

    public void deleteReservation(Long mapId, Long reservationId, ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        validateMapExistence(mapId);
        Reservation reservation = getReservation(reservationId, reservationPasswordAuthenticationRequest);
        reservationRepository.delete(reservation);
    }

    public ReservationResponse findReservation(final Long mapId, final Long reservationId, final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        validateMapExistence(mapId);
        Reservation reservation = getReservation(reservationId, reservationPasswordAuthenticationRequest);
        return ReservationResponse.of(reservation);
    }

    public void updateReservation(
            final Long mapId,
            final Long reservationId,
            final ReservationCreateUpdateRequest reservationCreateUpdateRequest) {
        validateMapExistence(mapId);

        LocalDateTime startDateTime = reservationCreateUpdateRequest.getStartDateTime();
        LocalDateTime endDateTime = reservationCreateUpdateRequest.getEndDateTime();

        validateTime(startDateTime, endDateTime);

        Space space = spaceRepository.findById(reservationCreateUpdateRequest.getSpaceId())
                .orElseThrow(NoSuchSpaceException::new);
        List<Reservation> reservations = getReservations(
                Collections.singletonList(space.getId()),
                startDateTime.toLocalDate());

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);
        if (reservation.getSpace().equals(space)) {
            reservations.remove(reservation);
        }
        validateAvailability(reservations, startDateTime, endDateTime);

        reservation.update(reservationCreateUpdateRequest, space);
        reservationRepository.save(reservation);
    }

    private void validateTime(final LocalDateTime startDateTime, final LocalDateTime endDateTime) {
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

    private void validateAvailability(final List<Reservation> reservations, final LocalDateTime startDateTime, final LocalDateTime endDateTime) {
        for (Reservation reservation : reservations) {
            if (reservation.hasConflictWith(startDateTime, endDateTime)) {
                throw new ImpossibleReservationTimeException();
            }
        }
    }

    private Reservation getReservation(final Long reservationId, final ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest) {
        Reservation reservation = reservationRepository
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);

        if (reservation.isWrongPassword(reservationPasswordAuthenticationRequest.getPassword())) {
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
