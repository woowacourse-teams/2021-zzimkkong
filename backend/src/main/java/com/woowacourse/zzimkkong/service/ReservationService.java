package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.ReservationFindResponse;
import com.woowacourse.zzimkkong.exception.NoSuchMapException;
import com.woowacourse.zzimkkong.dto.ReservationSaveRequest;
import com.woowacourse.zzimkkong.dto.ReservationSaveResponse;
import com.woowacourse.zzimkkong.exception.NoSuchSpaceException;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {
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

        List<Reservation> reservations = getReservations(spaceId, date);

        return ReservationFindResponse.of(reservations);
    }

    private List<Reservation> getReservations(final Long spaceId, final LocalDate date) {
        LocalDateTime minimumStartDateTime = date.atStartOfDay();
        LocalDateTime maximumStartDateTime = date.plusDays(1L).atStartOfDay();

        return reservationRepository.findAllBySpaceIdAndStartTimeIsBetweenAndEndTimeIsBetween(
                spaceId,
                minimumStartDateTime,
                maximumStartDateTime,
                minimumStartDateTime,
                maximumStartDateTime
        );
    }

    private void validateSpaceExistence(final Long spaceId) {
        if (!spaceRepository.existsById(spaceId)) {
            throw new NoSuchSpaceException();
        }
    }

    private void validateMapExistence(final Long mapId) {
        if (!mapRepository.existsById(mapId)) {
            throw new NoSuchMapException();
        }
    }
}
