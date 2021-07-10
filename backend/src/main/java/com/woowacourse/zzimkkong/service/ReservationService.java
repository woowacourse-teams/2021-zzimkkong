package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.ReservationSaveRequest;
import com.woowacourse.zzimkkong.dto.ReservationSaveResponse;
import com.woowacourse.zzimkkong.exception.ImpossibleReservationTimeException;
import com.woowacourse.zzimkkong.exception.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.NoSuchSpaceException;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReservationService {
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
        if (!mapRepository.existsById(mapId)) {
            throw new NoSuchMapException();
        }
        Space space = spaceRepository.findById(reservationSaveRequest.getSpaceId())
                .orElseThrow(NoSuchSpaceException::new);
        reservationSaveRequest.checkValidateTime();
        checkAlreadyExistReservationInTime(reservationSaveRequest, space);

        Reservation reservation = reservationRepository.save(
                new Reservation.Builder(reservationSaveRequest, space).build()
        );

        return ReservationSaveResponse.of(reservation);
    }

    private void checkAlreadyExistReservationInTime(ReservationSaveRequest reservationSaveRequest, Space space) {
        if (isPossibleStartTime(reservationSaveRequest, space) || isPossibleEndTime(reservationSaveRequest, space)) {
            throw new ImpossibleReservationTimeException();
        }
    }

    private Boolean isPossibleEndTime(ReservationSaveRequest reservationSaveRequest, Space space) {
        return reservationRepository.existsBySpaceIdAndEndTimeBetween(
                space.getId(),
                reservationSaveRequest.getStartDateTime().plusSeconds(1),
                reservationSaveRequest.getEndDateTime().minusSeconds(1));
    }

    private Boolean isPossibleStartTime(ReservationSaveRequest reservationSaveRequest, Space space) {
        return reservationRepository.existsBySpaceIdAndStartTimeBetween(
                space.getId(),
                reservationSaveRequest.getStartDateTime().plusSeconds(1),
                reservationSaveRequest.getEndDateTime().minusSeconds(1));
    }
}
