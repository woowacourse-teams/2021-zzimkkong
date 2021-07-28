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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

class SpaceServiceTest extends ServiceTest {
    @Autowired
    private SpaceService spaceService;
    private SettingsRequest settingsRequest = new SettingsRequest(
            LocalTime.of(10, 0),
            LocalTime.of(22, 0),
            30,
            60,
            120,
            true,
            "Monday, Tuesday"
    );

    private SpaceCreateRequest spaceCreateRequest = new SpaceCreateRequest(
            "백엔드 강의실",
            "우리집",
            "프론트 화이팅",
            settingsRequest,
            "이미지 입니다"
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
        SpaceCreateResponse spaceCreateResponse = spaceService.saveSpace(LUTHER.getId(), spaceCreateRequest, POBI);

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
        assertThatThrownBy(() -> spaceService.saveSpace(LUTHER.getId(), spaceCreateRequest, POBI))
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
        assertThatThrownBy(() -> spaceService.saveSpace(LUTHER.getId(), spaceCreateRequest, sakjung))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @DisplayName("공간 조회 시, spaceId를 가진 공간이 있다면 조회한다.")
    @Test
    void find() {
        // given
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findById(anyLong()))
                .willReturn(Optional.of(BE));

        // when
        SpaceFindResponse actual = spaceService.findSpace(1L, 1L);

        // then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(SpaceFindResponse.from(BE));
    }

    @DisplayName("공간 조회 시, spaceId에 맞는 공간이 없다면 예외를 발생시킨다.")
    @Test
    void findFail() {
        // given
        given(maps.existsById(anyLong()))
                .willReturn(true);
        given(spaces.findById(anyLong()))
                .willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> spaceService.findSpace(1L, 1L))
                .isInstanceOf(NoSuchSpaceException.class);
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
        SpaceFindAllResponse actual = spaceService.findAllSpace(1L, POBI);

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
        assertThatThrownBy(() -> spaceService.findAllSpace(1L, new Member("sakjung@email.com", "test1234", "잠실")))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }
}

