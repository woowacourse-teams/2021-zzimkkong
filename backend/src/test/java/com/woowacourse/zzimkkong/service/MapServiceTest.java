package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.map.MapCreateResponse;
import com.woowacourse.zzimkkong.dto.map.MapCreateUpdateRequest;
import com.woowacourse.zzimkkong.dto.map.MapFindAllResponse;
import com.woowacourse.zzimkkong.dto.map.MapFindResponse;
import com.woowacourse.zzimkkong.exception.authorization.NoAuthorityOnMapException;
import com.woowacourse.zzimkkong.exception.space.ReservationExistOnSpaceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.woowacourse.zzimkkong.service.ServiceTestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

class MapServiceTest extends ServiceTest {
    @Autowired
    private MapService mapService;

    @Test
    @DisplayName("맵 생성 요청 시, 올바른 요청이 들어오면 맵을 생성한다.")
    void create() {
        //given
        MapCreateUpdateRequest mapCreateUpdateRequest = new MapCreateUpdateRequest(LUTHER.getName(), LUTHER.getMapDrawing(), LUTHER.getMapImage());

        //when
        given(maps.save(any(Map.class)))
                .willReturn(LUTHER);

        //then
        MapCreateResponse mapCreateResponse = mapService.saveMap(POBI, mapCreateUpdateRequest);
        assertThat(mapCreateResponse.getId()).isEqualTo(LUTHER.getId());
    }

    @Test
    @DisplayName("맵 조회 요청 시, mapId에 해당하는 맵을 조회한다.")
    void find() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));

        //when
        MapFindResponse mapFindResponse = mapService.findMap(POBI, LUTHER.getId());

        //then
        assertThat(mapFindResponse).usingRecursiveComparison()
                .isEqualTo(MapFindResponse.from(LUTHER));
    }

    @Test
    @DisplayName("모든 맵 조회 요청 시, member가 가진 모든 맵을 조회한다.")
    void findAll() {
        //given
        List<Map> expectedMaps = List.of(LUTHER, SMALL_HOUSE);
        given(maps.findAllByMember(any(Member.class)))
                .willReturn(expectedMaps);

        //when
        MapFindAllResponse mapFindAllResponse = mapService.findAllMaps(POBI);

        //then
        assertThat(mapFindAllResponse).usingRecursiveComparison()
                .isEqualTo(MapFindAllResponse.from(expectedMaps));
    }

    @Test
    @DisplayName("맵 수정 요청이 들어오면 수정한다.")
    void update() {
        //given
        MapCreateUpdateRequest mapCreateUpdateRequest = new MapCreateUpdateRequest("이름을 바꿔요", LUTHER.getMapDrawing(), LUTHER.getMapImage());
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));

        //when, then
        assertDoesNotThrow(() -> mapService.updateMap(POBI, LUTHER.getId(), mapCreateUpdateRequest));
    }

    @Test
    @DisplayName("권한이 없는 관리자가 맵을 수정하려고 할 경우 예외가 발생한다.")
    void updateManagerException() {
        //given
        Member anotherMember = new Member("sally@email.com", "password", "organization");
        Map map = new Map(3L, "sally's home", "mapDrawing", "mapImage", anotherMember);
        MapCreateUpdateRequest mapCreateUpdateRequest = new MapCreateUpdateRequest("이름을 바꿔요", map.getMapDrawing(), map.getMapImage());

        given(maps.findById(anyLong()))
                .willReturn(Optional.of(map));

        // when, then
        assertThatThrownBy(() -> mapService.updateMap(POBI, map.getId(), mapCreateUpdateRequest))
                .isInstanceOf(NoAuthorityOnMapException.class);
    }

    @Test
    @DisplayName("맵 삭제 요청 시, 이후에 존재하는 예약이 없다면 삭제한다.")
    void delete() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));

        given(spaces.findAllByMapId(anyLong()))
                .willReturn(List.of(BE, FE1));

        given(reservations.existsBySpaceIdAndEndTimeAfter(anyLong(), any(LocalDateTime.class)))
                .willReturn(false);

        //when, then
        assertDoesNotThrow(() -> mapService.deleteMap(POBI, LUTHER.getId()));
    }

    @Test
    @DisplayName("맵 삭제 요청 시, 이후에 존재하는 예약이 있다면 예외가 발생한다.")
    void deleteExistReservationException() {
        //given
        given(maps.findById(anyLong()))
                .willReturn(Optional.of(LUTHER));

        given(spaces.findAllByMapId(anyLong()))
                .willReturn(List.of(BE, FE1));

        given(reservations.existsBySpaceIdAndEndTimeAfter(anyLong(), any(LocalDateTime.class)))
                .willReturn(true);

        //when, then
        assertThatThrownBy(() -> mapService.deleteMap(POBI, LUTHER.getId()))
                .isInstanceOf(ReservationExistOnSpaceException.class);
    }
}
