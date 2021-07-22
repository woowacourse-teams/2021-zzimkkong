package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.dto.space.SpaceFindResponse;
import com.woowacourse.zzimkkong.exception.space.NoSuchSpaceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.woowacourse.zzimkkong.CommonFixture.BE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

class SpaceServiceTest extends ServiceTest {
    @Autowired
    private SpaceService spaceService;

    @Test
    @DisplayName("공간 조회 시, spaceId를 가진 공간이 있다면 조회한다.")
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

    @Test
    @DisplayName("공간 조회 시, spaceId에 맞는 공간이 없다면 예외를 발생시킨다.")
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
}

