package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.reservation.*;
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

    public ReservationSaveResponse saveReservation(Long mapId, ReservationSaveRequest reservationSaveRequest) {
        validateMapExistence(mapId);

        Space space = spaceRepository.findById(reservationSaveRequest.getSpaceId())
                .orElseThrow(NoSuchSpaceException::new);
        validateReservation(reservationSaveRequest, space);

        Reservation reservation = reservationRepository.save(
                new Reservation.Builder()
                        .startTime(reservationSaveRequest.getStartDateTime())
                        .endTime(reservationSaveRequest.getEndDateTime())
                        .password(reservationSaveRequest.getPassword())
                        .userName(reservationSaveRequest.getName())
                        .description(reservationSaveRequest.getDescription())
                        .space(space)
                        .build());

        return ReservationSaveResponse.of(reservation);
    }

    private void validateReservation(final ReservationSaveRequest reservationSaveRequest, final Space space) {
        LocalDateTime startDateTime = reservationSaveRequest.getStartDateTime();
        LocalDateTime endDateTime = reservationSaveRequest.getEndDateTime();
        validateTime(startDateTime, endDateTime);
        validateAvailability(space, startDateTime, endDateTime);
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

    private void validateAvailability(final Space space, final LocalDateTime startDateTime, final LocalDateTime endDateTime) {
        List<Reservation> reservationsOnDate = getReservations(
                Collections.singletonList(space.getId()),
                startDateTime.toLocalDate());

        for (Reservation reservation : reservationsOnDate) {
            if (reservation.hasConflictWith(startDateTime, endDateTime)) {
                throw new ImpossibleReservationTimeException();
            }
        }
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

    public void deleteReservation(Long mapId, Long reservationId, ReservationDeleteRequest reservationDeleteRequest) {
        validateMapExistence(mapId);
        Reservation reservation = reservationRepository
                .findById(reservationId)
                .orElseThrow(NoSuchReservationException::new);

        if (reservation.isWrongPassword(reservationDeleteRequest.getPassword())) {
            throw new ReservationPasswordException();
        }
        reservationRepository.delete(reservation);
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
