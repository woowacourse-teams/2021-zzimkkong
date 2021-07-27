package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.dto.map.MapCreateRequest;
import com.woowacourse.zzimkkong.dto.map.MapCreateResponse;
import com.woowacourse.zzimkkong.dto.map.MapFindResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.woowacourse.zzimkkong.service.ServiceTestFixture.LUTHER;
import static com.woowacourse.zzimkkong.service.ServiceTestFixture.POBI;
import static org.assertj.core.api.Assertions.assertThat;
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
        MapCreateRequest mapCreateRequest = new MapCreateRequest(LUTHER.getName(), LUTHER.getMapDrawing(), LUTHER.getMapImage());

        //when
        given(maps.save(any(Map.class)))
                .willReturn(LUTHER);

        //then
        MapCreateResponse mapCreateResponse = mapService.saveMap(mapCreateRequest, POBI);
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
}
