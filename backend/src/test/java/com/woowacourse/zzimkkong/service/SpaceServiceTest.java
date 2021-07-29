package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.space.*;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;
import com.woowacourse.zzimkkong.exception.map.NoSuchMapException;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.woowacourse.zzimkkong.service.ServiceTestFixture.*;
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
            LocalTime.of(10, 0),
            LocalTime.of(22, 0),
            30,
            60,
            120,
            true,
            "Monday, Tuesday"
    );

    private SpaceCreateUpdateRequest spaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
            "백엔드 강의실",
            "우리집",
            SPACE_SVG,
            settingsRequest,
            MAP_SVG
    );

    private final SettingsRequest updateSettingsRequest = new SettingsRequest(
            LocalTime.of(10, 0),
            LocalTime.of(22, 0),
            40,
            60,
            120,
            true,
            "Monday, Wednesday"
    );
    private final SpaceCreateUpdateRequest updateSpaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
            "백엔드 강의실",
            "우리집",
            SPACE_SVG,
            updateSettingsRequest,
            MAP_SVG
    );

    @DisplayName("공간 생성 요청 시, 공간을 생성한다.")
    @Test
    void save() {
        // given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));
        given(spaces.save(any(Space.class)))
                .willReturn(BE);

        // when
        SpaceCreateResponse spaceCreateResponse = spaceService.saveSpace(LUTHER.getId(), spaceCreateUpdateRequest, POBI);

        // then
        assertThat(spaceCreateResponse.getId()).isEqualTo(BE.getId());
    }

    @DisplayName("공간 생성 요청 시, 맵이 존재하지 않는다면 예외가 발생한다.")
    @Test
    void saveNotExistMapException() {
        // given
        given(maps.findById(anyLong()))
                .willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> spaceService.saveSpace(LUTHER.getId(), spaceCreateUpdateRequest, POBI))
                .isInstanceOf(NoSuchMapException.class);
    }

    @DisplayName("공간 생성 요청 시, 맵에 대한 권한이 없다면 예외가 발생한다.")
    @Test
    void saveNoAuthorityOnMapException() {
        // given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));
        given(spaces.save(any(Space.class)))
                .willReturn(BE);

        Member sakjung = new Member(2L, "sakjung@naver.com", "test1234", "잠실킹");

        // when, then
        assertThatThrownBy(() -> spaceService.saveSpace(LUTHER.getId(), spaceCreateUpdateRequest, sakjung))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @DisplayName("공간 조회 시, spaceId를 가진 공간이 있다면 조회한다.")
    @Test
    void find() {
        // given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(BE));

        // when
        SpaceFindDetailResponse actual = spaceService.findSpace(LUTHER.getId(), BE.getId(), POBI);

        // then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(SpaceFindDetailResponse.from(BE));
    }
    
    @DisplayName("공간 조회 시, spaceId에 맞는 공간이 없다면 예외를 발생시킨다.")
    @Test
    void findFail() {
        // given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));
        given(spaces.findById(anyLong()))
                .willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> spaceService.findSpace(LUTHER.getId(), BE.getId(), POBI))
                .isInstanceOf(NoSuchSpaceException.class);
    }

    @DisplayName("공간 조회 시, 공간 관리자가 아니라면 예외를 발생시킨다.")
    @Test
    void findNoAuthorityOnMap() {
        // given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(BE));

        // when, then
        assertThatThrownBy(() -> spaceService.findSpace(LUTHER.getId(), BE.getId(), new Member("bada@bada.com", "test1234", "잠실")))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @DisplayName("전체 공간을 조회한다.")
    @Test
    void findAll() {
        // given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));
        given(spaces.findAllByMapId(anyLong()))
                .willReturn(List.of(BE, FE1));

        // when
        SpaceFindAllResponse actual = spaceService.findAllSpace(LUTHER.getId(), POBI);

        // then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(SpaceFindAllResponse.from(List.of(BE, FE1)));
    }

    @DisplayName("공간 전체 조회시, 공간 관리자가 아니라면 예외를 발생시킨다.")
    @Test
    void findAllNoAuthorityOnMap() {
        // given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));

        // when, then
        assertThatThrownBy(() -> spaceService.findAllSpace(LUTHER.getId(), new Member("sakjung@email.com", "test1234", "잠실")))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @DisplayName("공간을 수정한다.")
    @Test
    void update() {
        // given, when
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(BE));

        // then
        assertDoesNotThrow(() -> spaceService.updateSpace(
                LUTHER.getId(),
                BE.getId(),
                updateSpaceCreateUpdateRequest,
                POBI));

        assertThat(BE.getReservationTimeUnit()).isEqualTo(updateSettingsRequest.getReservationTimeUnit());
        assertThat(BE.getDisabledWeekdays()).isEqualTo(updateSettingsRequest.getDisabledWeekdays());
    }

    @DisplayName("공간 수정 요청 시, 해당 공간에 대한 권한이 없으면 수정할 수 없다.")
    @Test
    void updateNoAuthorityException() {
        // given, when
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(BE));

        // then
        assertThatThrownBy(() -> spaceService.updateSpace(
                LUTHER.getId(),
                BE.getId(),
                updateSpaceCreateUpdateRequest,
                new Member("ara", "test1234", "hihi")))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }
}

