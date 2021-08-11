package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Setting;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.space.*;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import com.woowacourse.zzimkkong.exception.space.ReservationExistOnSpaceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

class SpaceServiceTest extends ServiceTest {
    @Autowired
    private SpaceService spaceService;

    private final SettingsRequest settingsRequest = new SettingsRequest(
            BE_AVAILABLE_START_TIME,
            BE_AVAILABLE_END_TIME,
            BE_RESERVATION_TIME_UNIT,
            BE_RESERVATION_MINIMUM_TIME_UNIT,
            BE_RESERVATION_MAXIMUM_TIME_UNIT,
            BE_RESERVATION_ENABLE,
            BE_ENABLED_DAY_OF_WEEK
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
            "새로바뀐집",
            SPACE_DRAWING,
            settingsRequest,
            MAP_SVG
    );

    private Member pobi;
    private Member sakjung;
    private Map luther;
    private Space be;
    private Space fe;

    private Long noneExistingMapId;
    private Long noneExistingSpaceId;

    @BeforeEach
    void setUp() {
        pobi = new Member(EMAIL, PASSWORD, ORGANIZATION);
        sakjung = new Member(NEW_EMAIL, PASSWORD, ORGANIZATION);
        luther = new Map(1L, LUTHER_NAME, MAP_DRAWING_DATA, MAP_IMAGE_URL, pobi);

        Setting beSetting = Setting.builder()
                .availableStartTime(BE_AVAILABLE_START_TIME)
                .availableEndTime(BE_AVAILABLE_END_TIME)
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
                .availableStartTime(FE_AVAILABLE_START_TIME)
                .availableEndTime(FE_AVAILABLE_END_TIME)
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

        noneExistingMapId = luther.getId() + 1;
        noneExistingSpaceId = (long) (luther.getSpaces().size() + 1);
    }

    @Test
    @DisplayName("공간 생성 요청 시, 공간을 생성한다.")
    void save() {
        // given
        Setting setting = Setting.builder()
                .availableStartTime(BE_AVAILABLE_START_TIME)
                .availableEndTime(BE_AVAILABLE_END_TIME)
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build();

        Space newSpace = Space.builder()
                .id(3L)
                .name("새로운 공간")
                .map(luther)
                .description(BE_DESCRIPTION)
                .area(SPACE_DRAWING)
                .setting(setting)
                .build();

        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(spaces.save(any(Space.class)))
                .willReturn(newSpace);
        given(storageUploader.upload(anyString(), any(File.class)))
                .willReturn(MAP_IMAGE_URL);

        // when
        SpaceCreateResponse spaceCreateResponse = spaceService.saveSpace(luther.getId(), spaceCreateUpdateRequest, pobi);

        // then
        assertThat(spaceCreateResponse.getId()).isEqualTo(newSpace.getId());
    }

    @Test
    @DisplayName("공간 생성 요청 시, 맵이 존재하지 않는다면 예외가 발생한다.")
    void saveNotExistMapException() {
        // given
        given(maps.findById(anyLong()))
                .willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> spaceService.saveSpace(noneExistingMapId, spaceCreateUpdateRequest, pobi))
                .isInstanceOf(NoSuchMapException.class);
    }

    @Test
    @DisplayName("공간 생성 요청 시, 맵에 대한 권한이 없다면 예외가 발생한다.")
    void saveNoAuthorityOnMapException() {
        // given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(spaces.save(any(Space.class)))
                .willReturn(be);

        // when, then
        assertThatThrownBy(() -> spaceService.saveSpace(luther.getId(), spaceCreateUpdateRequest, sakjung))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("공간 조회 시, spaceId를 가진 공간이 있다면 조회한다.")
    void find() {
        // given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(be));

        // when
        SpaceFindDetailResponse actual = spaceService.findSpace(luther.getId(), be.getId(), pobi);

        // then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(SpaceFindDetailResponse.from(be));
    }

    @Test
    @DisplayName("공간 조회 시, spaceId에 맞는 공간이 없다면 예외를 발생시킨다.")
    void findFail() {
        // given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        // when, then
        assertThatThrownBy(() -> spaceService.findSpace(luther.getId(), noneExistingSpaceId, pobi))
                .isInstanceOf(NoSuchSpaceException.class);
    }

    @Test
    @DisplayName("공간 조회 시, 공간 관리자가 아니라면 예외를 발생시킨다.")
    void findNoAuthorityOnMap() {
        // given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(be));

        // when, then
        assertThatThrownBy(() -> spaceService.findSpace(luther.getId(), be.getId(), sakjung))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("전체 공간을 조회한다.")
    void findAll() {
        // given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        // when
        SpaceFindAllResponse actual = spaceService.findAllSpace(luther.getId(), pobi);

        // then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(SpaceFindAllResponse.from(List.of(be, fe)));
    }

    @Test
    @DisplayName("공간 전체 조회시, 공간 관리자가 아니라면 예외를 발생시킨다.")
    void findAllNoAuthorityOnMap() {
        // given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        // when, then
        assertThatThrownBy(() -> spaceService.findAllSpace(luther.getId(), sakjung))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("예약자 전체 공간을 조회한다.")
    void findAllGuest() {
        // given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));

        // when
        SpaceFindAllResponse actual = spaceService.findAllSpace(luther.getId());

        // then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(SpaceFindAllResponse.from(List.of(be, fe)));
    }

    @Test
    @DisplayName("공간을 수정한다.")
    void update() {
        // given, when
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(fe));
        given(storageUploader.upload(anyString(), any(File.class)))
                .willReturn(MAP_IMAGE_URL);

        // then
        assertDoesNotThrow(() -> spaceService.updateSpace(
                luther.getId(),
                fe.getId(),
                updateSpaceCreateUpdateRequest,
                pobi));

        assertThat(be.getReservationTimeUnit()).isEqualTo(settingsRequest.getReservationTimeUnit());
        assertThat(be.getEnabledDayOfWeek()).isEqualTo(settingsRequest.getEnabledDayOfWeek());
    }

    @Test
    @DisplayName("공간 수정 요청 시, 해당 공간에 대한 권한이 없으면 수정할 수 없다.")
    void updateNoAuthorityException() {
        // given, when
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(be));

        // then
        assertThatThrownBy(() -> spaceService.updateSpace(
                luther.getId(),
                be.getId(),
                updateSpaceCreateUpdateRequest,
                sakjung))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("공간 삭제 요청이 옳다면 삭제한다.")
    void deleteReservation() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(be));
        given(reservations.existsBySpaceIdAndEndTimeAfter(anyLong(), any(LocalDateTime.class)))
                .willReturn(false);
        SpaceDeleteRequest spaceDeleteRequest = new SpaceDeleteRequest(MAP_SVG);

        //then
        assertDoesNotThrow(() -> spaceService.deleteSpace(luther.getId(), be.getId(), spaceDeleteRequest, pobi));
    }

    @Test
    @DisplayName("공간 삭제 요청 시, 해당 맵의 관리자가 아니라면 오류가 발생한다.")
    void deleteNoAuthorityException() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        SpaceDeleteRequest spaceDeleteRequest = new SpaceDeleteRequest(MAP_SVG);

        //then
        assertThatThrownBy(() -> spaceService.deleteSpace(luther.getId(), be.getId(), spaceDeleteRequest, sakjung))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("공간 삭제 요청 시, 공간이 존재하지 않는다면 오류가 발생한다.")
    void deleteNoSuchSpaceException() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(spaces.findById(anyLong()))
                .willReturn(Optional.empty());
        SpaceDeleteRequest spaceDeleteRequest = new SpaceDeleteRequest(MAP_SVG);

        //then
        assertThatThrownBy(() -> spaceService.deleteSpace(luther.getId(), noneExistingSpaceId, spaceDeleteRequest, pobi))
                .isInstanceOf(NoSuchSpaceException.class);
    }

    @Test
    @DisplayName("공간 삭제 요청 시, 해당 공간에 예약이 존재한다면 오류가 발생한다.")
    void deleteReservationExistException() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(luther));
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(be));
        given(reservations.existsBySpaceIdAndEndTimeAfter(anyLong(), any(LocalDateTime.class)))
                .willReturn(true);
        SpaceDeleteRequest spaceDeleteRequest = new SpaceDeleteRequest(MAP_SVG);

        assertThatThrownBy(() -> spaceService.deleteSpace(luther.getId(), be.getId(), spaceDeleteRequest, pobi))
                .isInstanceOf(ReservationExistOnSpaceException.class);
    }
}
