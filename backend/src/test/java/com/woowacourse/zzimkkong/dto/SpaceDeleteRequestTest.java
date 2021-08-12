package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.dto.space.SpaceDeleteRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.EMPTY_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;

class SpaceDeleteRequestTest extends RequestTest {
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("맵의 Svg 정보에 빈 문자열이 들어오면 처리한다.")
    void blankMapImage(String mapImageSvg) {
        SpaceDeleteRequest spaceDeleteRequest = new SpaceDeleteRequest(mapImageSvg);

        assertThat(getConstraintViolations(spaceDeleteRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }
}
