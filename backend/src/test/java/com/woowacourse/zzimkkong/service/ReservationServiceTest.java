package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.ReservationSaveRequest;
import com.woowacourse.zzimkkong.dto.ReservationSaveResponse;
import com.woowacourse.zzimkkong.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

class ReservationServiceTest extends ServiceTest {
    public static final Member MEMBER = new Member("Sally@gmail.com", "1234", "Sally");
    public static final Map MAP = new Map("롯데몰", MEMBER);
    public static final Space SPACE = new Space("음식점", MAP);

    protected ReservationSaveRequest reservationSaveRequest;
    protected Reservation reservation;

    @Autowired
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationSaveRequest = new ReservationSaveRequest(
                1L, //TODO: 나중에 인수테스트 전부 생기면 갖다 쓰기
                LocalDateTime.now().plusDays(2).minusHours(2),
                LocalDateTime.now().plusDays(2).minusHours(1),
                "1234",
                "bada",
                "회의"
        );
        reservation = makeReservation(
                reservationSaveRequest.getStartDateTime(),
                reservationSaveRequest.getEndDateTime());
    }

    @Test
    @DisplayName("생성 요청 시, mapId와 요청이 들어온다면 예약을 생성한다.")
    void save() {
        //given

        //when
        given(mapRepository.existsById(anyLong()))
                .willReturn(true);
        given(spaceRepository.findById(anyLong()))
                .willReturn(Optional.of(SPACE));
        given(reservationRepository.save(any(Reservation.class)))
                .willReturn(reservation);

        ReservationSaveResponse reservationSaveResponse = reservationService.saveReservation(1L, reservationSaveRequest);

        //then
        assertThat(reservationSaveResponse.getId()).isEqualTo(reservation.getId());
    }

    @Test
    @DisplayName("생성 요청 시, mapId에 따른 map이 존재하지 않는다면 예외가 발생한다.")
    void saveNotExistMapException() {
        //given

        //when
        given(mapRepository.existsById(2L))
                .willReturn(false);
        given(spaceRepository.findById(anyLong()))
                .willReturn(Optional.of(SPACE));
        given(reservationRepository.save(any(Reservation.class)))
                .willReturn(reservation);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(1L, reservationSaveRequest))
                .isInstanceOf(NoSuchMapException.class);
    }

    @Test
    @DisplayName("생성 요청 시, spaceId에 따른 space가 존재하지 않는다면 예외가 발생한다.")
    void saveNotExistSpaceException() {
        //given

        //when
        given(mapRepository.existsById(anyLong()))
                .willReturn(true);
        given(spaceRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        given(reservationRepository.save(any(Reservation.class)))
                .willReturn(reservation);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(1L, reservationSaveRequest))
                .isInstanceOf(NoSuchSpaceException.class);
    }

    @Test
    @DisplayName("생성 요청 시, 시작 시간이 현재 시간보다 빠르다면 예외가 발생한다.")
    void saveStartTimeBeforeNow() {
        //given
        reservationSaveRequest = new ReservationSaveRequest(
                1L,
                LocalDateTime.now().minusHours(3),
                LocalDateTime.now().plusHours(3),
                "1234",
                "bada",
                "회의"
        );

        //when
        saveMock();

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(1L, reservationSaveRequest))
                .isInstanceOf(ImpossibleStartTimeException.class);
    }

    @Test
    @DisplayName("생성 요청 시, 종료 시간이 현재 시간보다 빠르다면 예외가 발생한다.")
    void saveEndTimeBeforeNow() {
        //given
        reservationSaveRequest = new ReservationSaveRequest(
                1L,
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().minusHours(3),
                "1234",
                "bada",
                "회의"
        );

        //when
        saveMock();

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(1L, reservationSaveRequest))
                .isInstanceOf(ImpossibleEndTimeException.class);
    }

    @Test
    @DisplayName("생성 요청 시, 시작 시간과 종료 시간이 같다면 예외가 발생한다.")
    void saveStartTimeEqualsEndTime() {
        //given
        LocalDateTime startDateTime = LocalDateTime.now().plusHours(3);
        reservationSaveRequest = new ReservationSaveRequest(
                1L,
                startDateTime,
                startDateTime,
                "1234",
                "bada",
                "회의"
        );

        //when
        saveMock();

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(1L, reservationSaveRequest))
                .isInstanceOf(ImpossibleEndTimeException.class);
    }

    @Test
    @DisplayName("생성 요청 시, 시작 시간과 종료 시간의 날짜가 다르다면 예외가 발생한다.")
    void saveStartTimeDateNotEqualsEndTimeDate() {
        //given
        reservationSaveRequest = new ReservationSaveRequest(
                1L,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3),
                "1234",
                "bada",
                "회의"
        );

        //when
        saveMock();

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(1L, reservationSaveRequest))
                .isInstanceOf(NonMatchingStartAndEndDateException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"-10:10", "2:0", "0:1", "60:59", "-59:-59"}, delimiter = ':')
    @DisplayName("생성 요청 시, 이미 겹치는 시간이 존재하면 예외가 발생한다.")
    void saveAvailabilityException(int startMinute, int endMinute) {
        //given

        //when
        saveMock();
        given(reservationRepository.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                any(Collection.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .willReturn(List.of(new Reservation.Builder()
                        .id(1L)
                        .startTime(reservationSaveRequest.getStartDateTime().minusMinutes(startMinute))
                        .endTime(reservationSaveRequest.getEndDateTime().minusMinutes(endMinute))
                        .password(reservationSaveRequest.getPassword())
                        .userName(reservationSaveRequest.getName())
                        .description(reservationSaveRequest.getDescription())
                        .space(SPACE)
                        .build()));

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(1L, reservationSaveRequest))
                .isInstanceOf(ImpossibleReservationTimeException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = 60)
    @DisplayName("생성 요청 시, 경계값이 일치한다면 생성된다.")
    void saveSameThresholdTime(int conferenceTime) {
        //given

        //when
        saveMock();
        given(reservationRepository.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                anyList(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .willReturn(List.of(
                        makeReservation(
                                reservationSaveRequest.getStartDateTime().minusMinutes(conferenceTime),
                                reservationSaveRequest.getEndDateTime().minusMinutes(conferenceTime)),
                        makeReservation(
                                reservationSaveRequest.getStartDateTime().plusMinutes(conferenceTime),
                                reservationSaveRequest.getEndDateTime().plusMinutes(conferenceTime))));

        //then
        ReservationSaveResponse reservationSaveResponse = reservationService.saveReservation(1L, reservationSaveRequest);
        assertThat(reservationSaveResponse.getId()).isEqualTo(reservation.getId());
    }

    private Reservation makeReservation(final LocalDateTime startTime, final LocalDateTime endTime) {
        return new Reservation.Builder()
                .id(1L)
                .startTime(startTime)
                .endTime(endTime)
                .password(reservationSaveRequest.getPassword())
                .userName(reservationSaveRequest.getName())
                .description(reservationSaveRequest.getDescription())
                .space(SPACE)
                .build();
    }

    private void saveMock() {
        given(mapRepository.existsById(anyLong()))
                .willReturn(true);
        given(spaceRepository.findById(anyLong()))
                .willReturn(Optional.of(SPACE));
        given(reservationRepository.save(any(Reservation.class)))
                .willReturn(reservation);
    }
}
