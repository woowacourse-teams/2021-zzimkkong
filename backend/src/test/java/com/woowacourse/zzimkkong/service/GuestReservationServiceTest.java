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

import static com.woowacourse.zzimkkong.service.ServiceTestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

class GuestReservationServiceTest extends ServiceTest {
    public static final ReservationPasswordAuthenticationRequest RESERVATION_PASSWORD_AUTHENTICATION_REQUEST
            = new ReservationPasswordAuthenticationRequest(RESERVATION_PASSWORD);

    @Autowired
    private GuestReservationService guestReservationService;

    private ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
            BE.getId(),
            THE_DAY_AFTER_TOMORROW_START_TIME.plusHours(3),
            THE_DAY_AFTER_TOMORROW_START_TIME.plusHours(4),
            RESERVATION_PASSWORD,
            USER_NAME,
            DESCRIPTION
    );

    private final Reservation reservation = makeReservation(
            reservationCreateUpdateWithPasswordRequest.getStartDateTime(),
            reservationCreateUpdateWithPasswordRequest.getEndDateTime(),
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
        ReservationCreateResponse reservationCreateResponse = guestReservationService.saveReservation(LUTHER.getId(), reservationCreateUpdateWithPasswordRequest);

        //then
        assertThat(reservationCreateResponse.getId()).isEqualTo(reservation.getId());
    }

    @Test
    @DisplayName("예약 생성 요청 시, mapId에 따른 map이 존재하지 않는다면 예외가 발생한다.")
    void saveNotExistMapException() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(false);
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(BE));
        given(reservations.save(any(Reservation.class)))
                .willReturn(reservation);

        //then
        assertThatThrownBy(() -> guestReservationService.saveReservation(1L, reservationCreateUpdateWithPasswordRequest))
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
        assertThatThrownBy(() -> guestReservationService.saveReservation(1L, reservationCreateUpdateWithPasswordRequest))
                .isInstanceOf(NoSuchSpaceException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 시작 시간이 현재 시간보다 빠르다면 예외가 발생한다.")
    void saveStartTimeBeforeNow() {
        //given
        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
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
        assertThatThrownBy(() -> guestReservationService.saveReservation(1L, reservationCreateUpdateWithPasswordRequest))
                .isInstanceOf(ImpossibleStartTimeException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 종료 시간이 현재 시간보다 빠르다면 예외가 발생한다.")
    void saveEndTimeBeforeNow() {
        //given
        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                1L,
                THE_DAY_AFTER_TOMORROW_START_TIME.plusHours(3),
                THE_DAY_AFTER_TOMORROW_START_TIME.minusHours(3),
                RESERVATION_PASSWORD,
                USER_NAME,
                DESCRIPTION
        );

        //when
        saveMock();

        //then
        assertThatThrownBy(() -> guestReservationService.saveReservation(1L, reservationCreateUpdateWithPasswordRequest))
                .isInstanceOf(ImpossibleEndTimeException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 시작 시간과 종료 시간이 같다면 예외가 발생한다.")
    void saveStartTimeEqualsEndTime() {
        //given
        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                1L,
                THE_DAY_AFTER_TOMORROW_START_TIME,
                THE_DAY_AFTER_TOMORROW_START_TIME,
                RESERVATION_PASSWORD,
                USER_NAME,
                DESCRIPTION
        );

        //when
        saveMock();

        //then
        assertThatThrownBy(() -> guestReservationService.saveReservation(1L, reservationCreateUpdateWithPasswordRequest))
                .isInstanceOf(ImpossibleEndTimeException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 시작 시간과 종료 시간의 날짜가 다르다면 예외가 발생한다.")
    void saveStartTimeDateNotEqualsEndTimeDate() {
        //given
        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                1L,
                THE_DAY_AFTER_TOMORROW_START_TIME,
                THE_DAY_AFTER_TOMORROW_START_TIME.plusDays(1),
                RESERVATION_PASSWORD,
                USER_NAME,
                DESCRIPTION
        );

        //when
        saveMock();

        //then
        assertThatThrownBy(() -> guestReservationService.saveReservation(1L, reservationCreateUpdateWithPasswordRequest))
                .isInstanceOf(NonMatchingStartAndEndDateException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"-30:0", "0:30"}, delimiter = ':')
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
                        reservationCreateUpdateWithPasswordRequest.getStartDateTime().minusMinutes(startMinute),
                        reservationCreateUpdateWithPasswordRequest.getEndDateTime().minusMinutes(endMinute),
                        BE)));

        //then
        assertThatThrownBy(() -> guestReservationService.saveReservation(
                LUTHER.getId(),
                reservationCreateUpdateWithPasswordRequest))
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
                                reservationCreateUpdateWithPasswordRequest.getStartDateTime().minusMinutes(conferenceTime),
                                reservationCreateUpdateWithPasswordRequest.getEndDateTime().minusMinutes(conferenceTime),
                                BE),
                        makeReservation(
                                reservationCreateUpdateWithPasswordRequest.getStartDateTime().plusMinutes(conferenceTime),
                                reservationCreateUpdateWithPasswordRequest.getEndDateTime().plusMinutes(conferenceTime),
                                BE)));

        //then
        ReservationCreateResponse reservationCreateResponse = guestReservationService.saveReservation(1L, reservationCreateUpdateWithPasswordRequest);
        assertThat(reservationCreateResponse.getId()).isEqualTo(reservation.getId());
    }

    @Test
    @DisplayName("특정 공간 예약 조회 요청 시, 올바르게 입력하면 해당 날짜, 공간에 대한 예약 정보가 조회된다.")
    void findReservations() {
        //given
        int conferenceTime = 30;
        List<Reservation> foundReservations = Arrays.asList(
                makeReservation(
                        reservationCreateUpdateWithPasswordRequest.getStartDateTime().minusMinutes(conferenceTime),
                        reservationCreateUpdateWithPasswordRequest.getEndDateTime().minusMinutes(conferenceTime),
                        BE),
                makeReservation(
                        reservationCreateUpdateWithPasswordRequest.getStartDateTime().plusMinutes(conferenceTime),
                        reservationCreateUpdateWithPasswordRequest.getEndDateTime().plusMinutes(conferenceTime),
                        BE));

        //when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(BE));
        given(reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                anyList(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .willReturn(foundReservations);

        //then
        ReservationFindResponse reservationFindResponse = ReservationFindResponse.from(foundReservations);
        assertThat(guestReservationService.findReservations(
                LUTHER.getId(),
                BE.getId(),
                THE_DAY_AFTER_TOMORROW))
                .usingRecursiveComparison()
                .isEqualTo(reservationFindResponse);
    }

    @Test
    @DisplayName("특정 공간 예약 조회 요청 시, 해당하는 공간이 없으면 오류가 발생한다.")
    void findReservationsNotExistSpace() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findAllByMapId(anyLong()))
                .willReturn(List.of(BE, FE1));
        //then
        assertThatThrownBy(() -> guestReservationService.findReservations(
                LUTHER.getId(),
                BE.getId(),
                THE_DAY_AFTER_TOMORROW))
                .isInstanceOf(NoSuchSpaceException.class);
    }

    @Test
    @DisplayName("전체 예약이나 특정 공간 예약 조회 요청 시, 해당하는 예약이 없으면 빈 정보가 조회된다.")
    void findEmptyReservations() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findAllByMapId(anyLong()))
                .willReturn(List.of(BE, FE1));
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(BE));
        given(reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                anyList(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .willReturn(Collections.emptyList());

        //then
        ReservationFindResponse reservationFindResponse = ReservationFindResponse.from(Collections.emptyList());
        assertThat(guestReservationService.findReservations(
                LUTHER.getId(),
                BE.getId(),
                THE_DAY_AFTER_TOMORROW))
                .usingRecursiveComparison()
                .isEqualTo(reservationFindResponse);
        assertThat(guestReservationService.findAllReservations(LUTHER.getId(), THE_DAY_AFTER_TOMORROW))
                .usingRecursiveComparison()
                .isEqualTo(ReservationFindAllResponse.of(List.of(BE, FE1), Collections.emptyList()));
    }

    @Test
    @DisplayName("전체 예약 조회 요청 시, 올바른 mapId, 날짜를 입력하면 해당 날짜에 존재하는 모든 예약 정보가 공간의 Id를 기준으로 정렬되어 조회된다.")
    void findAllReservation() {
        int conferenceTime = 30;
        List<Reservation> foundReservations = List.of(
                makeReservation(
                        reservationCreateUpdateWithPasswordRequest.getStartDateTime().minusMinutes(conferenceTime),
                        reservationCreateUpdateWithPasswordRequest.getEndDateTime().minusMinutes(conferenceTime),
                        BE),
                makeReservation(
                        reservationCreateUpdateWithPasswordRequest.getStartDateTime().plusMinutes(conferenceTime),
                        reservationCreateUpdateWithPasswordRequest.getEndDateTime().plusMinutes(conferenceTime),
                        BE),
                makeReservation(
                        reservationCreateUpdateWithPasswordRequest.getStartDateTime().minusMinutes(conferenceTime),
                        reservationCreateUpdateWithPasswordRequest.getEndDateTime().minusMinutes(conferenceTime),
                        FE1),
                makeReservation(
                        reservationCreateUpdateWithPasswordRequest.getStartDateTime().plusMinutes(conferenceTime),
                        reservationCreateUpdateWithPasswordRequest.getEndDateTime().plusMinutes(conferenceTime),
                        FE1));
        List<Space> findSpaces = List.of(BE, FE1);


        //when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findAllByMapId(anyLong()))
                .willReturn(findSpaces);
        given(reservations.findAllBySpaceIdInAndStartTimeIsBetweenAndEndTimeIsBetween(
                anyList(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .willReturn(foundReservations);

        //then
        ReservationFindAllResponse reservationFindAllResponse = ReservationFindAllResponse.of(findSpaces, foundReservations);
        assertThat(guestReservationService.findAllReservations(LUTHER.getId(), THE_DAY_AFTER_TOMORROW))
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
        ReservationResponse actualResponse = guestReservationService.findReservation(
                1L,
                this.reservation.getId(),
                new ReservationPasswordAuthenticationRequest(this.reservation.getPassword()));

        //then
        assertThat(actualResponse).usingRecursiveComparison()
                .isEqualTo(ReservationResponse.from(reservation));
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
        assertThatThrownBy(() -> guestReservationService.findReservation(
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
        assertThatThrownBy(() -> guestReservationService.findReservation(
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
        assertDoesNotThrow(() -> guestReservationService.updateReservation(1L, reservation.getId(), new ReservationCreateUpdateWithPasswordRequest(
                1L,
                THE_DAY_AFTER_TOMORROW.atTime(10,0),
                THE_DAY_AFTER_TOMORROW.atTime(11,0),
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
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                1L,
                LocalDateTime.now().plusDays(1L).plusHours(1),
                LocalDateTime.now().plusDays(1L).plusHours(endTime),
                reservation.getPassword(),
                CHANGED_NAME,
                CHANGED_DESCRIPTION
        );

        //then
        assertThatThrownBy(() -> guestReservationService.updateReservation(1L, 1L, reservationCreateUpdateWithPasswordRequest))
                .isInstanceOf(ImpossibleEndTimeException.class);
    }

    @Test
    @DisplayName("예약 수정 요청 시, 시작 시간과 끝 시간이 같은 날짜가 아니면 에러가 발생한다.")
    void updateInvalidDateException() {
        //given
        given(maps.existsById(anyLong()))
                .willReturn(true);

        //when
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                reservation.getPassword(),
                CHANGED_NAME,
                CHANGED_DESCRIPTION
        );

        //then
        assertThatThrownBy(() -> guestReservationService.updateReservation(1L, 1L, reservationCreateUpdateWithPasswordRequest))
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
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                1L,
                reservation.getStartTime(),
                reservation.getEndTime(),
                "1231",
                CHANGED_NAME,
                CHANGED_DESCRIPTION
        );

        //then
        assertThatThrownBy(() -> guestReservationService.updateReservation(1L, 1L, reservationCreateUpdateWithPasswordRequest))
                .isInstanceOf(ReservationPasswordException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"30:30", "-30:-30"}, delimiter = ':')
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
        ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                1L,
                BE_PM_ONE_TWO.getStartTime().plusMinutes(startTime),
                BE_PM_ONE_TWO.getEndTime().plusMinutes(endTime),
                reservation.getPassword(),
                reservation.getUserName(),
                reservation.getDescription()
        );

        //then
        assertThatThrownBy(() -> guestReservationService.updateReservation(1L, 1L, reservationCreateUpdateWithPasswordRequest))
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
                        THE_DAY_AFTER_TOMORROW_START_TIME,
                        THE_DAY_AFTER_TOMORROW_START_TIME.plusHours(2),
                        BE)));

        //then
        assertDoesNotThrow(() -> guestReservationService.deleteReservation(1L, 1L, RESERVATION_PASSWORD_AUTHENTICATION_REQUEST));
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
        assertThatThrownBy(() -> guestReservationService.deleteReservation(1L, 1L, RESERVATION_PASSWORD_AUTHENTICATION_REQUEST))
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
                        THE_DAY_AFTER_TOMORROW_START_TIME.plusHours(3),
                        THE_DAY_AFTER_TOMORROW_START_TIME.plusHours(4),
                        BE)));

        //then
        assertThatThrownBy(() -> guestReservationService.deleteReservation(1L, 1L, reservationPasswordAuthenticationRequest))
                .isInstanceOf(ReservationPasswordException.class);
    }

    private Reservation makeReservation(final LocalDateTime startTime, final LocalDateTime endTime, final Space space) {
        return new Reservation.Builder()
                .id(1L)
                .startTime(startTime)
                .endTime(endTime)
                .password(reservationCreateUpdateWithPasswordRequest.getPassword())
                .userName(reservationCreateUpdateWithPasswordRequest.getName())
                .description(reservationCreateUpdateWithPasswordRequest.getDescription())
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
