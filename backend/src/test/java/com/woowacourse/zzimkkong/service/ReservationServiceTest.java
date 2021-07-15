package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.reservation.*;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.woowacourse.zzimkkong.CommonFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

class ReservationServiceTest extends ServiceTest {
    public static final ReservationPasswordAuthenticationRequest RESERVATION_PASSWORD_AUTHENTICATION_REQUEST
            = new ReservationPasswordAuthenticationRequest(RESERVATION_PASSWORD);

    @Autowired
    private ReservationService reservationService;

    private ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
            1L,
            TOMORROW_START_TIME.plusHours(3),
            TOMORROW_START_TIME.plusHours(4),
            RESERVATION_PASSWORD,
            USER_NAME,
            DESCRIPTION
    );

    private final Reservation reservation = makeReservation(
            reservationCreateUpdateRequest.getStartDateTime(),
            reservationCreateUpdateRequest.getEndDateTime(),
            BE);
    public static final String CHANGED_NAME = "이름 변경";
    public static final String CHANGED_DESCRIPTION = "회의명 변경";

    @Test
    @DisplayName("예약 생성 요청 시, mapId와 요청이 들어온다면 예약을 생성한다.")
    void save() {
        //given
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(BE));
        given(reservations.save(any(Reservation.class)))
                .willReturn(reservation);

        //when
        ReservationCreateResponse reservationCreateResponse = reservationService.saveReservation(1L, reservationCreateUpdateRequest);

        //then
        assertThat(reservationCreateResponse.getId()).isEqualTo(reservation.getId());
    }

    @Test
    @DisplayName("예약 생성 요청 시, mapId에 따른 map이 존재하지 않는다면 예외가 발생한다.")
    void saveNotExistMapException() {
        //given, when
        given(maps.existsById(2L))
                .willReturn(false);
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(BE));
        given(reservations.save(any(Reservation.class)))
                .willReturn(reservation);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(1L, reservationCreateUpdateRequest))
                .isInstanceOf(NoSuchMapException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, spaceId에 따른 space가 존재하지 않는다면 예외가 발생한다.")
    void saveNotExistSpaceException() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findById(anyLong()))
                .willReturn(Optional.empty());
        given(reservations.save(any(Reservation.class)))
                .willReturn(reservation);

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(1L, reservationCreateUpdateRequest))
                .isInstanceOf(NoSuchSpaceException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 시작 시간이 현재 시간보다 빠르다면 예외가 발생한다.")
    void saveStartTimeBeforeNow() {
        //given
        reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                1L,
                LocalDateTime.now().minusHours(3),
                LocalDateTime.now().plusHours(3),
                RESERVATION_PASSWORD,
                USER_NAME,
                DESCRIPTION
        );

        //when
        saveMock();

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(1L, reservationCreateUpdateRequest))
                .isInstanceOf(ImpossibleStartTimeException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 종료 시간이 현재 시간보다 빠르다면 예외가 발생한다.")
    void saveEndTimeBeforeNow() {
        //given
        reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                1L,
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().minusHours(3),
                RESERVATION_PASSWORD,
                USER_NAME,
                DESCRIPTION
        );

        //when
        saveMock();

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(1L, reservationCreateUpdateRequest))
                .isInstanceOf(ImpossibleEndTimeException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 시작 시간과 종료 시간이 같다면 예외가 발생한다.")
    void saveStartTimeEqualsEndTime() {
        //given
        reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                1L,
                TOMORROW_START_TIME,
                TOMORROW_START_TIME,
                RESERVATION_PASSWORD,
                USER_NAME,
                DESCRIPTION
        );

        //when
        saveMock();

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(1L, reservationCreateUpdateRequest))
                .isInstanceOf(ImpossibleEndTimeException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 시작 시간과 종료 시간의 날짜가 다르다면 예외가 발생한다.")
    void saveStartTimeDateNotEqualsEndTimeDate() {
        //given
        reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                1L,
                TOMORROW_START_TIME,
                TOMORROW_START_TIME.plusDays(1),
                RESERVATION_PASSWORD,
                USER_NAME,
                DESCRIPTION
        );

        //when
        saveMock();

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(1L, reservationCreateUpdateRequest))
                .isInstanceOf(NonMatchingStartAndEndDateException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"-10:10", "2:0", "0:1", "60:59", "-59:-59"}, delimiter = ':')
    @DisplayName("예약 생성 요청 시, 이미 겹치는 시간이 존재하면 예외가 발생한다.")
    void saveAvailabilityException(int startMinute, int endMinute) {
        //given, when
        saveMock();
        given(reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                any(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .willReturn(List.of(makeReservation(
                        reservationCreateUpdateRequest.getStartDateTime().minusMinutes(startMinute),
                        reservationCreateUpdateRequest.getEndDateTime().minusMinutes(endMinute),
                        BE)));

        //then
        assertThatThrownBy(() -> reservationService.saveReservation(1L, reservationCreateUpdateRequest))
                .isInstanceOf(ImpossibleReservationTimeException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = 60)
    @DisplayName("예약 생성 요청 시, 경계값이 일치한다면 생성된다.")
    void saveSameThresholdTime(int conferenceTime) {
        //given, when
        saveMock();
        given(reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                anyList(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .willReturn(List.of(
                        makeReservation(
                                reservationCreateUpdateRequest.getStartDateTime().minusMinutes(conferenceTime),
                                reservationCreateUpdateRequest.getEndDateTime().minusMinutes(conferenceTime),
                                BE),
                        makeReservation(
                                reservationCreateUpdateRequest.getStartDateTime().plusMinutes(conferenceTime),
                                reservationCreateUpdateRequest.getEndDateTime().plusMinutes(conferenceTime),
                                BE)));

        //then
        ReservationCreateResponse reservationCreateResponse = reservationService.saveReservation(1L, reservationCreateUpdateRequest);
        assertThat(reservationCreateResponse.getId()).isEqualTo(reservation.getId());
    }

    @Test
    @DisplayName("특정 공간 예약 조회 요청 시, 올바르게 입력하면 해당 날짜, 공간에 대한 예약 정보가 조회된다.")
    void findReservations() {
        //given
        int conferenceTime = 30;
        List<Reservation> foundReservations = Arrays.asList(
                makeReservation(
                        reservationCreateUpdateRequest.getStartDateTime().minusMinutes(conferenceTime),
                        reservationCreateUpdateRequest.getEndDateTime().minusMinutes(conferenceTime),
                        BE),
                makeReservation(
                        reservationCreateUpdateRequest.getStartDateTime().plusMinutes(conferenceTime),
                        reservationCreateUpdateRequest.getEndDateTime().plusMinutes(conferenceTime),
                        BE));

        //when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.existsById(anyLong()))
                .willReturn(true);
        given(reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                anyList(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .willReturn(foundReservations);

        //then
        ReservationFindResponse reservationFindResponse = ReservationFindResponse.of(foundReservations);
        assertThat(reservationService.findReservations(1L, 1L, TOMORROW))
                .usingRecursiveComparison()
                .isEqualTo(reservationFindResponse);
    }

    @Test
    @DisplayName("특정 공간 예약 조회 요청 시, 해당하는 공간이 없으면 오류가 발생한다.")
    void findReservationsNotExistSpace() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.existsById(anyLong()))
                .willReturn(false);
        //then
        assertThatThrownBy(() -> reservationService.findReservations(1L, 1L, TOMORROW))
                .isInstanceOf(NoSuchSpaceException.class);
    }

    @Test
    @DisplayName("전체 예약이나 특정 공간 예약 조회 요청 시, 해당하는 예약이 없으면 빈 정보가 조회된다.")
    void findEmptyReservations() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.existsById(anyLong()))
                .willReturn(true);
        given(reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                anyList(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .willReturn(Collections.emptyList());

        //then
        ReservationFindResponse reservationFindResponse = ReservationFindResponse.of(Collections.emptyList());
        assertThat(reservationService.findReservations(1L, 1L, TOMORROW))
                .usingRecursiveComparison()
                .isEqualTo(reservationFindResponse);
        assertThat(reservationService.findAllReservations(1L, TOMORROW))
                .usingRecursiveComparison()
                .isEqualTo(ReservationFindAllResponse.of(Collections.emptyList()));
    }

    @Test
    @DisplayName("전체 예약 조회 요청 시, 올바른 mapId, 날짜를 입력하면 해당 날짜에 존재하는 모든 예약 정보가 조회된다.")
    void findAllReservation() {
        //given
        int conferenceTime = 30;
        List<Reservation> foundReservations = List.of(
                makeReservation(
                        reservationCreateUpdateRequest.getStartDateTime().minusMinutes(conferenceTime),
                        reservationCreateUpdateRequest.getEndDateTime().minusMinutes(conferenceTime),
                        BE),
                makeReservation(
                        reservationCreateUpdateRequest.getStartDateTime().plusMinutes(conferenceTime),
                        reservationCreateUpdateRequest.getEndDateTime().plusMinutes(conferenceTime),
                        BE),
                makeReservation(
                        reservationCreateUpdateRequest.getStartDateTime().minusMinutes(conferenceTime),
                        reservationCreateUpdateRequest.getEndDateTime().minusMinutes(conferenceTime),
                        FE1),
                makeReservation(
                        reservationCreateUpdateRequest.getStartDateTime().plusMinutes(conferenceTime),
                        reservationCreateUpdateRequest.getEndDateTime().plusMinutes(conferenceTime),
                        FE1));

        //when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findAllByMapId(anyLong()))
                .willReturn(List.of(BE, FE1));
        given(reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                anyList(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .willReturn(foundReservations);

        //then
        ReservationFindAllResponse reservationFindAllResponse = ReservationFindAllResponse.of(foundReservations);
        assertThat(reservationService.findAllReservations(1L, TOMORROW))
                .usingRecursiveComparison()
                .isEqualTo(reservationFindAllResponse);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 비밀번호가 일치하는지 확인하고 해당 예약을 반환한다.")
    void findReservation() {
        //given
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when
        ReservationResponse actual = reservationService.findReservation(
                1L,
                this.reservation.getId(),
                new ReservationPasswordAuthenticationRequest(this.reservation.getPassword()));

        //then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(ReservationResponse.of(reservation));
    }

    @Test
    @DisplayName("예약 수정 요청 시, 해당 예약이 존재하지 않으면 에러가 발생한다.")
    void findInvalidReservationException() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(reservations.findById(anyLong()))
                .willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> reservationService.findReservation(
                1L,
                reservation.getId(),
                new ReservationPasswordAuthenticationRequest("1111")))
                .isInstanceOf(NoSuchReservationException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 비밀번호가 일치하지 않으면 에러가 발생한다.")
    void findWrongPasswordException() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //then
        assertThatThrownBy(() -> reservationService.findReservation(
                1L,
                reservation.getId(),
                new ReservationPasswordAuthenticationRequest("1111")))
                .isInstanceOf(ReservationPasswordException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 올바른 요청이 들어오면 예약이 수정된다.")
    void update() {
        //given
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(BE));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when, then
        assertDoesNotThrow(() -> reservationService.updateReservation(1L, reservation.getId(), new ReservationCreateUpdateRequest(
                1L,
                TOMORROW_START_TIME.plusHours(20),
                TOMORROW_START_TIME.plusHours(21),
                reservation.getPassword(),
                CHANGED_NAME,
                CHANGED_DESCRIPTION
        )));
        assertThat(reservation.getUserName()).isEqualTo(CHANGED_NAME);
        assertThat(reservation.getDescription()).isEqualTo(CHANGED_DESCRIPTION);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("예약 수정 요청 시, 끝 시간 입력이 옳지 않으면 에러가 발생한다.")
    void updateInvalidEndTimeException(int endTime) {
        //given
        given(maps.existsById(anyLong()))
                .willReturn(true);

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(endTime),
                reservation.getPassword(),
                CHANGED_NAME,
                CHANGED_DESCRIPTION
        );

        //then
        assertThatThrownBy(() -> reservationService.updateReservation(1L, 1L, reservationCreateUpdateRequest))
                .isInstanceOf(ImpossibleEndTimeException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 시작 시간과 끝 시간이 같은 날짜가 아니면 에러가 발생한다.")
    void updateInvalidDateException() {
        //given
        given(maps.existsById(anyLong()))
                .willReturn(true);

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                reservation.getPassword(),
                CHANGED_NAME,
                CHANGED_DESCRIPTION
        );

        //then
        assertThatThrownBy(() -> reservationService.updateReservation(1L, 1L, reservationCreateUpdateRequest))
                .isInstanceOf(NonMatchingStartAndEndDateException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 비밀번호가 일치하지 않으면 에러가 발생한다.")
    void updateIncorrectPasswordException() {
        //given
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(BE));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                1L,
                reservation.getStartTime(),
                reservation.getEndTime(),
                "1231",
                CHANGED_NAME,
                CHANGED_DESCRIPTION
        );

        //then
        assertThatThrownBy(() -> reservationService.updateReservation(1L, 1L, reservationCreateUpdateRequest))
                .isInstanceOf(ReservationPasswordException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 변경 사항이 존재하지 않으면 에러가 발생한다.")
    void updateNothingChangedException() {
        //given
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(BE));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                1L,
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getPassword(),
                reservation.getUserName(),
                reservation.getDescription()
        );

        //then
        assertThatThrownBy(() -> reservationService.updateReservation(1L, 1L, reservationCreateUpdateRequest))
                .isInstanceOf(NoDataToUpdateException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"0:2", "59: 70", "-10:10"}, delimiter = ':')
    @DisplayName("예약 수정 요청 시, 해당 시간에 예약이 존재하면 에러가 발생한다.")
    void updateImpossibleTimeException(int startTime, int endTime) {
        //given
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(BE));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));
        given(reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(anyList(), any(), any(), any(), any()))
                .willReturn(Arrays.asList(
                        BE_AM_ZERO_ONE,
                        BE_PM_ONE_TWO
                ));

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                1L,
                BE_PM_ONE_TWO.getStartTime().plusMinutes(startTime),
                BE_PM_ONE_TWO.getStartTime().plusMinutes(endTime),
                reservation.getPassword(),
                reservation.getUserName(),
                reservation.getDescription()
        );

        //then
        assertThatThrownBy(() -> reservationService.updateReservation(1L, 1L, reservationCreateUpdateRequest))
                .isInstanceOf(ImpossibleReservationTimeException.class);
    }

    @Test
    @DisplayName("예약 삭제 요청이 옳다면 삭제한다.")
    void deleteReservation() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(makeReservation(
                        TOMORROW_START_TIME,
                        TOMORROW_START_TIME.plusHours(2),
                        BE)));

        //then
        assertDoesNotThrow(() -> reservationService.deleteReservation(1L, 1L, RESERVATION_PASSWORD_AUTHENTICATION_REQUEST));
    }

    @Test
    @DisplayName("예약 삭제 요청 시, 예약이 존재하지 않는다면 오류가 발생한다.")
    void deleteReservationException() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(reservations.findById(anyLong()))
                .willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> reservationService.deleteReservation(1L, 1L, RESERVATION_PASSWORD_AUTHENTICATION_REQUEST))
                .isInstanceOf(NoSuchReservationException.class);
    }

    @Test
    @DisplayName("예약 삭제 요청 시, 비밀번호가 일치하지 않는다면 오류가 발생한다.")
    void deleteReservationPasswordException() {
        //given
        ReservationPasswordAuthenticationRequest reservationPasswordAuthenticationRequest
                = new ReservationPasswordAuthenticationRequest("1233");

        //when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(makeReservation(
                        TOMORROW_START_TIME.plusHours(3),
                        TOMORROW_START_TIME.plusHours(4),
                        BE)));

        //then
        assertThatThrownBy(() -> reservationService.deleteReservation(1L, 1L, reservationPasswordAuthenticationRequest))
                .isInstanceOf(ReservationPasswordException.class);
    }

    private Reservation makeReservation(final LocalDateTime startTime, final LocalDateTime endTime, final Space space) {
        return new Reservation.Builder()
                .id(1L)
                .startTime(startTime)
                .endTime(endTime)
                .password(reservationCreateUpdateRequest.getPassword())
                .userName(reservationCreateUpdateRequest.getName())
                .description(reservationCreateUpdateRequest.getDescription())
                .space(space)
                .build();
    }

    private void saveMock() {
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(BE));
        given(reservations.save(any(Reservation.class)))
                .willReturn(reservation);
    }
}
