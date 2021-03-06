package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.member.LoginEmailDto;
import com.woowacourse.zzimkkong.dto.space.*;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.exception.space.ReservationExistOnSpaceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

class SpaceServiceTest extends ServiceTest {
    @Autowired
    private SpaceService spaceService;

    private final SettingsRequest settingsRequest = new SettingsRequest(
            BE_AVAILABLE_START_TIME,
            BE_AVAILABLE_END_TIME,
            BE_RESERVATION_TIME_UNIT.getMinutes(),
            BE_RESERVATION_MINIMUM_TIME_UNIT.getMinutes(),
            BE_RESERVATION_MAXIMUM_TIME_UNIT.getMinutes(),
            BE_RESERVATION_ENABLE,
            EnabledDayOfWeekDto.from(BE_ENABLED_DAY_OF_WEEK)
    );

    private final SpaceCreateUpdateRequest spaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
            BE_NAME,
            BE_COLOR,
            BE_DESCRIPTION,
            SPACE_DRAWING,
            settingsRequest,
            MAP_SVG
    );

    private final SpaceCreateUpdateRequest updateSpaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
            BE_NAME,
            "#FFCCE5",
            "???????????????",
            SPACE_DRAWING,
            settingsRequest,
            MAP_SVG
    );

    private Member pobi;
    private Member sakjung;
    private LoginEmailDto pobiEmail;
    private LoginEmailDto sakjungEmail;
    private Map luther;
    private Space be;
    private Space fe;

    private Long lutherId;
    private Long beId;
    private Long noneExistingMapId;
    private Long noneExistingSpaceId;

    @BeforeEach
    void setUp() {
        pobi = new Member(EMAIL, PW, ORGANIZATION);
        sakjung = new Member(NEW_EMAIL, PW, ORGANIZATION);
        pobiEmail = LoginEmailDto.from(EMAIL);
        sakjungEmail = LoginEmailDto.from(NEW_EMAIL);
        luther = new Map(1L, LUTHER_NAME, MAP_DRAWING_DATA, MAP_SVG, pobi);

        Setting beSetting = Setting.builder()
                .availableTimeSlot(TimeSlot.of(
                        BE_AVAILABLE_START_TIME,
                        BE_AVAILABLE_END_TIME))
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build();

        be = Space.builder()
                .id(1L)
                .name(BE_NAME)
                .map(luther)
                .description(BE_DESCRIPTION)
                .area(SPACE_DRAWING)
                .setting(beSetting)
                .build();

        Setting feSetting = Setting.builder()
                .availableTimeSlot(TimeSlot.of(
                        FE_AVAILABLE_START_TIME,
                        FE_AVAILABLE_END_TIME))
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build();

        fe = Space.builder()
                .id(2L)
                .name(FE_NAME)
                .color(FE_COLOR)
                .map(luther)
                .description(FE_DESCRIPTION)
                .area(SPACE_DRAWING)
                .setting(feSetting)
                .build();

        lutherId = luther.getId();
        beId = be.getId();
        noneExistingMapId = luther.getId() + 1;
        noneExistingSpaceId = (long) (luther.getSpaces().size() + 1);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ???, ????????? ????????????.")
    void save() {
        // given
        Setting setting = Setting.builder()
                .availableTimeSlot(TimeSlot.of(
                        BE_AVAILABLE_START_TIME,
                        BE_AVAILABLE_END_TIME))
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build();

        Space newSpace = Space.builder()
                .id(3L)
                .name("????????? ??????")
                .map(luther)
                .description(BE_DESCRIPTION)
                .area(SPACE_DRAWING)
                .setting(setting)
                .build();

        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(spaces.save(any(Space.class)))
                .willReturn(newSpace);

        // when
        SpaceCreateResponse spaceCreateResponse = spaceService.saveSpace(luther.getId(), spaceCreateUpdateRequest, pobiEmail);

        // then
        assertThat(spaceCreateResponse.getId()).isEqualTo(newSpace.getId());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ???, ?????? ???????????? ???????????? ????????? ????????????.")
    void saveNotExistMapException() {
        // given
        given(maps.findById(anyLong()))
                .willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> spaceService.saveSpace(noneExistingMapId, spaceCreateUpdateRequest, pobiEmail))
                .isInstanceOf(NoSuchMapException.class);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ???, ?????? ?????? ????????? ????????? ????????? ????????????.")
    void saveNoAuthorityOnMapException() {
        // given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(spaces.save(any(Space.class)))
                .willReturn(be);

        // when, then
        assertThatThrownBy(() -> spaceService.saveSpace(lutherId, spaceCreateUpdateRequest, sakjungEmail))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("?????? ?????? ???, spaceId??? ?????? ????????? ????????? ????????????.")
    void find() {
        // given
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));

        // when
        SpaceFindDetailResponse actual = spaceService.findSpace(luther.getId(), be.getId(), pobiEmail);

        // then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(SpaceFindDetailResponse.from(be));
    }

    @Test
    @DisplayName("?????? ?????? ???, spaceId??? ?????? ????????? ????????? ????????? ???????????????.")
    void findFail() {
        // given
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));

        // when, then
        assertThatThrownBy(() -> spaceService.findSpace(lutherId, noneExistingSpaceId, pobiEmail))
                .isInstanceOf(NoSuchSpaceException.class);
    }

    @Test
    @DisplayName("?????? ?????? ???, ?????? ???????????? ???????????? ????????? ???????????????.")
    void findNoAuthorityOnMap() {
        // given
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(be));

        // when, then
        assertThatThrownBy(() -> spaceService.findSpace(lutherId, beId, sakjungEmail))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("?????? ????????? ????????????.")
    void findAll() {
        // given
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));

        // when
        SpaceFindAllResponse actual = spaceService.findAllSpace(luther.getId(), pobiEmail);

        // then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(SpaceFindAllResponse.from(List.of(be, fe)));
    }

    @Test
    @DisplayName("?????? ?????? ?????????, ?????? ???????????? ???????????? ????????? ???????????????.")
    void findAllNoAuthorityOnMap() {
        // given
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));

        // when, then
        assertThatThrownBy(() -> spaceService.findAllSpace(lutherId, sakjungEmail))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("????????? ?????? ????????? ????????????.")
    void findAllGuest() {
        // given
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));

        // when
        SpaceFindAllResponse actual = spaceService.findAllSpace(luther.getId());

        // then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(SpaceFindAllResponse.from(List.of(be, fe)));
    }

    @Test
    @DisplayName("????????? ????????????.")
    void update() {
        // given, when
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));

        // then
        assertDoesNotThrow(() -> spaceService.updateSpace(
                luther.getId(),
                fe.getId(),
                updateSpaceCreateUpdateRequest,
                pobiEmail));

        assertThat(be.getReservationTimeUnitAsInt()).isEqualTo(settingsRequest.getReservationTimeUnit());
        assertThat(be.getEnabledDayOfWeek()).isEqualTo(settingsRequest.enabledDayOfWeekAsString());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ???, ?????? ????????? ?????? ????????? ????????? ????????? ??? ??????.")
    void updateNoAuthorityException() {
        // given, when
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));

        // then
        assertThatThrownBy(() -> spaceService.updateSpace(
                lutherId,
                beId,
                updateSpaceCreateUpdateRequest,
                sakjungEmail))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("?????? ?????? ????????? ????????? ????????????.")
    void deleteReservation() {
        //given
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.existsBySpaceIdAndReservationTimeEndTimeAfter(anyLong(), any(LocalDateTime.class)))
                .willReturn(false);
        SpaceDeleteRequest spaceDeleteRequest = new SpaceDeleteRequest(MAP_SVG);

        //then
        assertDoesNotThrow(() -> spaceService.deleteSpace(luther.getId(), be.getId(), spaceDeleteRequest, pobiEmail));
    }

    @Test
    @DisplayName("?????? ?????? ?????? ???, ?????? ?????? ???????????? ???????????? ????????? ????????????.")
    void deleteNoAuthorityException() {
        //given
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        SpaceDeleteRequest spaceDeleteRequest = new SpaceDeleteRequest(MAP_SVG);

        //then
        assertThatThrownBy(() -> spaceService.deleteSpace(lutherId, beId, spaceDeleteRequest, sakjungEmail))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ???, ????????? ???????????? ???????????? ????????? ????????????.")
    void deleteNoSuchSpaceException() {
        //given
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        SpaceDeleteRequest spaceDeleteRequest = new SpaceDeleteRequest(MAP_SVG);

        //then
        assertThatThrownBy(() -> spaceService.deleteSpace(lutherId, noneExistingSpaceId, spaceDeleteRequest, pobiEmail))
                .isInstanceOf(NoSuchSpaceException.class);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ???, ?????? ????????? ????????? ??????????????? ????????? ????????????.")
    void deleteReservationExistException() {
        //given
        given(maps.findByIdFetch(anyLong()))
                .willReturn(Optional.of(luther));
        given(reservations.existsBySpaceIdAndReservationTimeEndTimeAfter(anyLong(), any(LocalDateTime.class)))
                .willReturn(true);
        SpaceDeleteRequest spaceDeleteRequest = new SpaceDeleteRequest(MAP_SVG);

        assertThatThrownBy(() -> spaceService.deleteSpace(lutherId, beId, spaceDeleteRequest, pobiEmail))
                .isInstanceOf(ReservationExistOnSpaceException.class);
    }
}
