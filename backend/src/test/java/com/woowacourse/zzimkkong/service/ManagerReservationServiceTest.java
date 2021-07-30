package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.reservation.*;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;
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

public class ManagerReservationServiceTest extends ServiceTest {
    public static final String CHANGED_NAME = "이름 변경";
    public static final String CHANGED_DESCRIPTION = "회의명 변경";
    @Autowired
    private ManagerReservationService managerReservationService;
    private ReservationCreateUpdateWithPasswordRequest reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
            BE.getId(),
            TOMORROW_START_TIME.plusHours(3),
            TOMORROW_START_TIME.plusHours(4),
            RESERVATION_PASSWORD,
            USER_NAME,
            DESCRIPTION
    );
    private final Reservation reservation = makeReservation(
            reservationCreateUpdateWithPasswordRequest.getStartDateTime(),
            reservationCreateUpdateWithPasswordRequest.getEndDateTime(),
            BE);

    @DisplayName("예약 생성 요청 시, mapId와 요청이 들어온다면 예약을 생성한다.")
    @Test
    void save() {
        //given
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(BE));
        given(reservations.save(any(Reservation.class)))
                .willReturn(reservation);

        //when
        ReservationCreateResponse reservationCreateResponse = managerReservationService.saveReservation(
                LUTHER.getId(),
                reservationCreateUpdateWithPasswordRequest,
                POBI);

        //then
        assertThat(reservationCreateResponse.getId()).isEqualTo(reservation.getId());
    }

    @DisplayName("예약 생성 요청 시, mapId에 따른 map이 존재하지 않는다면 예외가 발생한다.")
    @Test
    void saveNotExistMapException() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(false);

        //then
        assertThatThrownBy(() -> managerReservationService.saveReservation(
                LUTHER.getId(),
                reservationCreateUpdateWithPasswordRequest,
                POBI))
                .isInstanceOf(NoSuchMapException.class);
    }

    @DisplayName("예약 생성 요청 시, map에 대한 권한이 없다면 예외가 발생한다.")
    @Test
    void saveNoAuthorityOnMapException() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));

        //then
        assertThatThrownBy(() -> managerReservationService.saveReservation(
                LUTHER.getId(),
                reservationCreateUpdateWithPasswordRequest,
                JASON))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @DisplayName("예약 생성 요청 시, spaceId에 따른 space가 존재하지 않는다면 예외가 발생한다.")
    @Test
    void saveNotExistSpaceException() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));
        given(spaces.findById(anyLong()))
                .willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> managerReservationService.saveReservation(
                LUTHER.getId(),
                reservationCreateUpdateWithPasswordRequest,
                POBI))
                .isInstanceOf(NoSuchSpaceException.class);
    }

    @DisplayName("예약 생성 요청 시, 시작 시간이 현재 시간보다 빠르다면 예외가 발생한다.")
    @Test
    void saveStartTimeBeforeNow() {
        //given
        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                BE.getId(),
                LocalDateTime.now().minusHours(3),
                LocalDateTime.now().plusHours(3),
                RESERVATION_PASSWORD,
                USER_NAME,
                DESCRIPTION
        );

        //when
        saveMock();

        //then
        assertThatThrownBy(() -> managerReservationService.saveReservation(
                LUTHER.getId(),
                reservationCreateUpdateWithPasswordRequest,
                POBI))
                .isInstanceOf(ImpossibleStartTimeException.class);
    }

    @DisplayName("예약 생성 요청 시, 종료 시간이 현재 시간보다 빠르다면 예외가 발생한다.")
    @Test
    void saveEndTimeBeforeNow() {
        //given
        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                BE.getId(),
                TOMORROW_START_TIME.plusHours(3),
                TOMORROW_START_TIME.minusHours(3),
                RESERVATION_PASSWORD,
                USER_NAME,
                DESCRIPTION
        );

        //when
        saveMock();

        //then
        assertThatThrownBy(() -> managerReservationService.saveReservation(
                LUTHER.getId(),
                reservationCreateUpdateWithPasswordRequest,
                POBI))
                .isInstanceOf(ImpossibleEndTimeException.class);
    }

    @DisplayName("예약 생성 요청 시, 시작 시간과 종료 시간이 같다면 예외가 발생한다.")
    @Test
    void saveStartTimeEqualsEndTime() {
        //given
        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                BE.getId(),
                TOMORROW_START_TIME,
                TOMORROW_START_TIME,
                RESERVATION_PASSWORD,
                USER_NAME,
                DESCRIPTION
        );

        //when
        saveMock();

        //then
        assertThatThrownBy(() -> managerReservationService.saveReservation(
                LUTHER.getId(),
                reservationCreateUpdateWithPasswordRequest,
                POBI))
                .isInstanceOf(ImpossibleEndTimeException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 시작 시간과 종료 시간의 날짜가 다르다면 예외가 발생한다.")
    void saveStartTimeDateNotEqualsEndTimeDate() {
        //given
        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                BE.getId(),
                TOMORROW_START_TIME,
                TOMORROW_START_TIME.plusDays(1),
                RESERVATION_PASSWORD,
                USER_NAME,
                DESCRIPTION
        );

        //when
        saveMock();

        //then
        assertThatThrownBy(() -> managerReservationService.saveReservation(
                LUTHER.getId(),
                reservationCreateUpdateWithPasswordRequest,
                POBI))
                .isInstanceOf(NonMatchingStartAndEndDateException.class);
    }

    @Test
    @DisplayName("예약 생성 요청 시, 공간의 예약가능 시간이 아니라면 예외가 발생한다.")
    void saveInvalidTimeSetting() {
        //given
        reservationCreateUpdateWithPasswordRequest = new ReservationCreateUpdateWithPasswordRequest(
                BE.getId(),
                TOMORROW.atTime(9,59),
                TOMORROW.atTime(22,1),
                RESERVATION_PASSWORD,
                USER_NAME,
                DESCRIPTION
        );

        //when
        saveMock();

        //then
        assertThatThrownBy(() -> managerReservationService.saveReservation(
                LUTHER.getId(),
                reservationCreateUpdateWithPasswordRequest,
                POBI))
                .isInstanceOf(ConflictSpaceSettingException.class);
    }

    @DisplayName("예약 생성 요청 시, 이미 겹치는 시간이 존재하면 예외가 발생한다.")
    @ParameterizedTest
    @CsvSource(value = {"-10:10", "2:0", "0:1", "60:59", "-59:-59"}, delimiter = ':')
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
        assertThatThrownBy(() -> managerReservationService.saveReservation(
                LUTHER.getId(),
                reservationCreateUpdateWithPasswordRequest,
                POBI))
                .isInstanceOf(ImpossibleReservationTimeException.class);
    }

    @DisplayName("예약 생성 요청 시, 경계값이 일치한다면 생성된다.")
    @ParameterizedTest
    @ValueSource(ints = 60)
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
        ReservationCreateResponse reservationCreateResponse = managerReservationService.saveReservation(
                LUTHER.getId(),
                reservationCreateUpdateWithPasswordRequest,
                POBI);
        assertThat(reservationCreateResponse.getId()).isEqualTo(reservation.getId());
    }

    @DisplayName("특정 공간 예약 조회 요청 시, 올바르게 입력하면 해당 날짜, 공간에 대한 예약 정보가 조회된다.")
    @Test
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
        ReservationFindResponse reservationFindResponse = ReservationFindResponse.from(foundReservations);
        assertThat(managerReservationService.findReservations(LUTHER.getId(), BE.getId(), TOMORROW))
                .usingRecursiveComparison()
                .isEqualTo(reservationFindResponse);
    }

    @DisplayName("특정 공간 예약 조회 요청 시, 해당하는 맵이 없으면 오류가 발생한다.")
    @Test
    void findReservationsNotExistMap() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(false);
        //then
        assertThatThrownBy(() -> managerReservationService.findReservations(LUTHER.getId(), BE.getId(), TOMORROW))
                .isInstanceOf(NoSuchMapException.class);
    }

    @DisplayName("특정 공간 예약 조회 요청 시, 해당하는 공간이 없으면 오류가 발생한다.")
    @Test
    void findReservationsNotExistSpace() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.existsById(anyLong()))
                .willReturn(false);
        //then
        assertThatThrownBy(() -> managerReservationService.findReservations(LUTHER.getId(), BE.getId(), TOMORROW))
                .isInstanceOf(NoSuchSpaceException.class);
    }

    @DisplayName("전체 예약이나 특정 공간 예약 조회 요청 시, 해당하는 예약이 없으면 빈 정보가 조회된다.")
    @Test
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
        assertThat(managerReservationService.findReservations(LUTHER.getId(), BE.getId(), TOMORROW))
                .usingRecursiveComparison()
                .isEqualTo(ReservationFindResponse.from(Collections.emptyList()));
        assertThat(managerReservationService.findAllReservations(LUTHER.getId(), TOMORROW))
                .usingRecursiveComparison()
                .isEqualTo(ReservationFindAllResponse.from(Collections.emptyList()));
    }

    @DisplayName("전체 예약 조회 요청 시, 올바른 mapId, 날짜를 입력하면 해당 날짜에 존재하는 모든 예약 정보가 조회된다.")
    @Test
    void findAllReservation() {
        //given
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
        ReservationFindAllResponse reservationFindAllResponse = ReservationFindAllResponse.from(foundReservations);
        assertThat(managerReservationService.findAllReservations(LUTHER.getId(), TOMORROW))
                .usingRecursiveComparison()
                .isEqualTo(reservationFindAllResponse);
    }

    @DisplayName("예약 수정을 위한 예약 조회 요청 시, 해당 예약을 반환한다.")
    @Test
    void findReservation() {
        //given
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when
        ReservationResponse actualResponse = managerReservationService.findReservation(
                LUTHER.getId(),
                this.reservation.getId(),
                POBI);

        //then
        assertThat(actualResponse).usingRecursiveComparison()
                .isEqualTo(ReservationResponse.from(reservation));
    }

    @DisplayName("예약 수정을 위한 예약 조회 요청 시, 해당 맵에 대한 권한이 없으면 조회를 할 수 없다.")
    @Test
    void findReservation_NoAuthority() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));

        //then
        assertThatThrownBy(() -> managerReservationService.findReservation(
                LUTHER.getId(),
                reservation.getId(),
                JASON))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @DisplayName("예약 수정 요청 시, 해당 예약이 존재하지 않으면 에러가 발생한다.")
    @Test
    void findInvalidReservationException() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> managerReservationService.findReservation(
                LUTHER.getId(),
                reservation.getId(),
                POBI))
                .isInstanceOf(NoSuchReservationException.class);
    }

    @DisplayName("예약 수정 요청 시, 올바른 요청이 들어오면 예약이 수정된다.")
    @Test
    void update() {
        //given
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(BE));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        // when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                BE.getId(),
                TOMORROW_START_TIME.plusHours(20),
                TOMORROW_START_TIME.plusHours(21),
                CHANGED_NAME,
                CHANGED_DESCRIPTION
        );

        //then
        assertDoesNotThrow(() -> managerReservationService.updateReservation(
                LUTHER.getId(),
                reservation.getId(),
                reservationCreateUpdateRequest,
                POBI));
        assertThat(reservation.getUserName()).isEqualTo(CHANGED_NAME);
        assertThat(reservation.getDescription()).isEqualTo(CHANGED_DESCRIPTION);
    }

    @DisplayName("예약 수정 요청 시, 해당 맵에 대한 권한이 없으면 수정할 수 없다.")
    @Test
    void updateNoAuthorityException() {
        //given
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                BE.getId(),
                TOMORROW_START_TIME.plusHours(20),
                TOMORROW_START_TIME.plusHours(21),
                CHANGED_NAME,
                CHANGED_DESCRIPTION
        );

        //then
        assertThatThrownBy(() -> managerReservationService.updateReservation(
                LUTHER.getId(),
                1L,
                reservationCreateUpdateRequest,
                JASON))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @DisplayName("예약 수정 요청 시, 끝 시간 입력이 옳지 않으면 에러가 발생한다.")
    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void updateInvalidEndTimeException(int endTime) {
        //given
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                BE.getId(),
                LocalDateTime.now().plusDays(1L).plusHours(1),
                LocalDateTime.now().plusDays(1L).plusHours(endTime),
                CHANGED_NAME,
                CHANGED_DESCRIPTION
        );

        //then
        assertThatThrownBy(() -> managerReservationService.updateReservation(
                LUTHER.getId(),
                1L,
                reservationCreateUpdateRequest,
                POBI))
                .isInstanceOf(ImpossibleEndTimeException.class);
    }

    @DisplayName("예약 수정 요청 시, 시작 시간과 끝 시간이 같은 날짜가 아니면 에러가 발생한다.")
    @Test
    void updateInvalidDateException() {
        //given
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                BE.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                CHANGED_NAME,
                CHANGED_DESCRIPTION
        );

        //then
        assertThatThrownBy(() -> managerReservationService.updateReservation(
                LUTHER.getId(),
                1L,
                reservationCreateUpdateRequest,
                POBI))
                .isInstanceOf(NonMatchingStartAndEndDateException.class);
    }

    @DisplayName("예약 수정 요청 시, 해당 시간에 예약이 존재하면 에러가 발생한다.")
    @ParameterizedTest
    @CsvSource(value = {"0:2", "59:70", "-10:10"}, delimiter = ':')
    void updateImpossibleTimeException(int startTime, int endTime) {
        //given
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));
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
                BE.getId(),
                BE_PM_ONE_TWO.getStartTime().plusMinutes(startTime),
                BE_PM_ONE_TWO.getStartTime().plusMinutes(endTime),
                reservation.getUserName(),
                reservation.getDescription()
        );

        //then
        assertThatThrownBy(() -> managerReservationService.updateReservation(
                LUTHER.getId(), 1L,
                reservationCreateUpdateRequest,
                POBI))
                .isInstanceOf(ImpossibleReservationTimeException.class);
    }

    @DisplayName("예약 수정 요청 시, 공간의 예약가능 시간이 아니라면 에러가 발생한다.")
    @Test
    void updateInvalidTimeSetting() {
        //given
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(BE));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservation));

        //when
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                BE.getId(),
                TOMORROW.atTime(9,59),
                TOMORROW.atTime(22,1),
                CHANGED_NAME,
                CHANGED_DESCRIPTION
        );

        //then
        assertThatThrownBy(() -> managerReservationService.updateReservation(
                LUTHER.getId(),
                1L,
                reservationCreateUpdateRequest,
                POBI))
                .isInstanceOf(ConflictSpaceSettingException.class);
    }

    @DisplayName("예약 삭제 요청이 옳다면 삭제한다.")
    @Test
    void deleteReservation() {
        //given, when
        Reservation reservationToDelete = makeReservation(
                TOMORROW_START_TIME,
                TOMORROW_START_TIME.plusHours(2),
                BE);

        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.of(reservationToDelete));

        //then
        assertDoesNotThrow(() -> managerReservationService.deleteReservation(LUTHER.getId(), 1L, POBI));
    }

    @DisplayName("예약 삭제 요청 시, 맵의 관리자가 아니라면 오류가 발생한다.")
    @Test
    void deleteNoAuthorityException() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> managerReservationService.deleteReservation(LUTHER.getId(), 1L, JASON))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @DisplayName("예약 삭제 요청 시, 예약이 존재하지 않는다면 오류가 발생한다.")
    @Test
    void deleteReservationException() {
        //given, when
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));
        given(reservations.findById(anyLong()))
                .willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> managerReservationService.deleteReservation(LUTHER.getId(), 1L, POBI))
                .isInstanceOf(NoSuchReservationException.class);
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
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(BE));
        given(reservations.save(any(Reservation.class)))
                .willReturn(reservation);
    }
}
