package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.admin.*;
import com.woowacourse.zzimkkong.dto.map.MapFindResponse;
import com.woowacourse.zzimkkong.dto.member.MemberFindResponse;
import com.woowacourse.zzimkkong.dto.reservation.ReservationResponse;
import com.woowacourse.zzimkkong.dto.space.SpaceFindDetailWithIdResponse;
import com.woowacourse.zzimkkong.exception.member.PasswordMismatchException;
import com.woowacourse.zzimkkong.infrastructure.SharingIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class AdminServiceTest extends ServiceTest {
    private Member pobi;

    @Autowired
    private AdminService adminService;

    @MockBean
    private SharingIdGenerator sharingIdGenerator;

    @BeforeEach
    void setUp() {
        pobi = new Member(EMAIL, passwordEncoder.encode(PW), ORGANIZATION);
    }

    @Test
    @DisplayName("어드민 관리자 로그인 아이디, 비밀번호가 옳지 않으면 에러가 발생한다.")
    void loginException() {
        assertThatThrownBy(() -> adminService.login("zzimkkong", "wrong"))
                .isInstanceOf(PasswordMismatchException.class);
    }

    @Test
    @DisplayName("모든 멤버를 페이지네이션을 이용해 조회한다.")
    void findMembers() {
        //given
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.unsorted());
        given(members.findAll(any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(pobi), pageRequest, 1));

        //when
        MembersResponse membersResponse = MembersResponse.from(
                List.of(MemberFindResponse.from(pobi)),
                PageInfo.from(0, 1, 20, 1)
        );
        MembersResponse members = adminService.findMembers(pageRequest);

        //then
        assertThat(members).usingRecursiveComparison()
                .isEqualTo(membersResponse);
    }

    @Test
    @DisplayName("모든 맵을 페이지네이션을 이용해 조회한다.")
    void findMaps() {
        //given
        Map luther = new Map(LUTHER_NAME, MAP_DRAWING_DATA, MAP_IMAGE_URL, pobi);
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.unsorted());
        given(maps.findAll(any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(luther), pageRequest, 1));
        given(sharingIdGenerator.from(any(Map.class)))
                .willReturn("someId");
        //when
        MapsResponse mapsResponse = MapsResponse.from(
                List.of(MapFindResponse.ofAdmin(luther, "someId")),
                PageInfo.from(0, 1, 20, 1)
        );
        MapsResponse maps = adminService.findMaps(pageRequest);

        //then
        assertThat(maps).usingRecursiveComparison()
                .isEqualTo(mapsResponse);
    }

    @Test
    @DisplayName("모든 공간을 페이지네이션을 이용해 조회한다.")
    void findSpaces() {
        //given
        Map luther = new Map(LUTHER_NAME, MAP_DRAWING_DATA, MAP_IMAGE_URL, pobi);
        Setting beSetting = Setting.builder()
                .availableStartTime(BE_AVAILABLE_START_TIME)
                .availableEndTime(BE_AVAILABLE_END_TIME)
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build();

        Space be = Space.builder()
                .id(1L)
                .name(BE_NAME)
                .map(luther)
                .description(BE_DESCRIPTION)
                .area(SPACE_DRAWING)
                .setting(beSetting)
                .build();

        PageRequest pageRequest = PageRequest.of(0, 20, Sort.unsorted());
        given(spaces.findAll(any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(be), pageRequest, 1));

        //when
        SpacesResponse expected = SpacesResponse.from(
                List.of(SpaceFindDetailWithIdResponse.fromAdmin(be)),
                PageInfo.from(0, 1, 20, 1)
        );
        SpacesResponse actual = adminService.findSpaces(pageRequest);

        //then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(expected);
    }


    @Test
    @DisplayName("모든 예약을 페이지네이션을 이용해 조회한다.")
    void findReservations() {
        //given
        Map luther = new Map(1L, LUTHER_NAME, MAP_DRAWING_DATA, MAP_IMAGE_URL, pobi);
        Setting beSetting = Setting.builder()
                .availableStartTime(BE_AVAILABLE_START_TIME)
                .availableEndTime(BE_AVAILABLE_END_TIME)
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build();

        Space be = Space.builder()
                .id(1L)
                .name(BE_NAME)
                .map(luther)
                .description(BE_DESCRIPTION)
                .area(SPACE_DRAWING)
                .setting(beSetting)
                .build();

        Reservation beAmZeroOne = Reservation.builder()
                .date(BE_AM_TEN_ELEVEN_START_TIME.toLocalDate())
                .startTime(BE_AM_TEN_ELEVEN_START_TIME)
                .endTime(BE_AM_TEN_ELEVEN_END_TIME)
                .description(BE_AM_TEN_ELEVEN_DESCRIPTION)
                .userName(BE_AM_TEN_ELEVEN_USERNAME)
                .password(BE_AM_TEN_ELEVEN_PW)
                .space(be)
                .build();

        PageRequest pageRequest = PageRequest.of(0, 20, Sort.unsorted());
        given(reservations.findAll(any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(beAmZeroOne), pageRequest, 1));

        //when
        ReservationsResponse expected = ReservationsResponse.from(
                List.of(ReservationResponse.fromAdmin(beAmZeroOne)),
                PageInfo.from(0, 1, 20, 1)
        );
        ReservationsResponse actual = adminService.findReservations(pageRequest);

        //then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
