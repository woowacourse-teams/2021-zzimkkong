package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.ReservationFindAllResponse;
import com.woowacourse.zzimkkong.dto.ReservationFindResponse;
import com.woowacourse.zzimkkong.dto.ReservationSaveRequest;
import com.woowacourse.zzimkkong.dto.ReservationSaveResponse;
import com.woowacourse.zzimkkong.exception.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.NoSuchSpaceException;
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

    public ReservationService(MapRepository mapRepository, SpaceRepository spaceRepository, ReservationRepository reservationRepository) {
        this.mapRepository = mapRepository;
        this.spaceRepository = spaceRepository;
        this.reservationRepository = reservationRepository;
    }

    public ReservationSaveResponse saveReservation(Long mapId, ReservationSaveRequest reservationSaveRequest) {
        validateMapExistence(mapId);

        Space space = spaceRepository.findById(reservationSaveRequest.getSpaceId())
                .orElseThrow(NoSuchSpaceException::new);
        Reservation reservation = reservationRepository.save(
                new Reservation(
                        reservationSaveRequest.getStartDateTime(),
                        reservationSaveRequest.getEndDateTime(),
                        reservationSaveRequest.getPassword(),
                        reservationSaveRequest.getName(),
                        reservationSaveRequest.getDescription(),
                        space)
        );

        return ReservationSaveResponse.of(reservation);
    }

    public ReservationFindResponse find(final Long mapId, final Long spaceId, final LocalDate date) {
        validateMapExistence(mapId);
        validateSpaceExistence(spaceId);

        List<Reservation> reservations = getReservations(Collections.singletonList(spaceId), date);

        return ReservationFindResponse.of(reservations);
    }

    public ReservationFindAllResponse findAll(final Long mapId, final LocalDate date) {
        validateMapExistence(mapId);

        List<Long> spaceIds = spaceRepository.findAllByMapId(mapId)
                .stream()
                .map(Space::getId)
                .collect(Collectors.toList());

        List<Reservation> reservations = getReservations(spaceIds, date);

        return ReservationFindAllResponse.of(reservations);
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
