package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.dto.space.SpaceCreateUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.EMPTY_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;

class SpaceCreateUpdateRequestTest extends RequestTest {
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("공간 이름에 빈 문자열이 들어오면 처리한다.")
    void blankReservationPassword(String name) {
        SpaceCreateUpdateRequest spaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
                name,
                "color",
                "description",
                "area",
                "mapImageSvg",
                true,
                List.of(beSettingRequest));

        assertThat(getConstraintViolations(spaceCreateUpdateRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("공간 설명에 빈 문자열이 들어오면 처리한다.")
    void blankReservationDescription(String description) {
        SpaceCreateUpdateRequest spaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
                "name",
                "color",
                description,
                "area",
                "mapImageSvg",
                true,
                List.of(beSettingRequest));

        assertThat(getConstraintViolations(spaceCreateUpdateRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("공간 area에 빈 문자열이 들어오면 처리한다.")
    void blankReservationArea(String area) {
        SpaceCreateUpdateRequest spaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
                "name",
                "color",
                "description",
                area,
                "mapImageSvg",
                true,
                List.of(beSettingRequest));

        assertThat(getConstraintViolations(spaceCreateUpdateRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("맵의 Svg 정보에 빈 문자열이 들어오면 처리한다.")
    void blankMapImageSvg(String mapImageSvg) {
        SpaceCreateUpdateRequest spaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
                "name",
                "color",
                "description",
                "area",
                mapImageSvg,
                true,
                List.of(beSettingRequest));

        assertThat(getConstraintViolations(spaceCreateUpdateRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }
}
