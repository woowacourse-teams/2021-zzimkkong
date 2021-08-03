package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.CommonFixture;
import com.woowacourse.zzimkkong.dto.reservation.ReservationPasswordAuthenticationRequest;
import com.woowacourse.zzimkkong.dto.space.SpaceCreateUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.EMPTY_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SpaceCreateUpdateRequestTest extends RequestTest{
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("공간 이름에 빈 문자열이 들어오면 처리한다.")
    public void blankReservationPassword(String name) {
        SpaceCreateUpdateRequest spaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
                name,
                "description",
                "area",
                CommonFixture.beSettingsRequest,
                "image");

        assertThat(getConstraintViolations(spaceCreateUpdateRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("공간 설명에 빈 문자열이 들어오면 처리한다.")
    public void blankReservationDescription(String description) {
        SpaceCreateUpdateRequest spaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
                "name",
                description,
                "area",
                CommonFixture.beSettingsRequest,
                "image");

        assertThat(getConstraintViolations(spaceCreateUpdateRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("공간 area에 빈 문자열이 들어오면 처리한다.")
    public void blankReservationArea(String area) {
        SpaceCreateUpdateRequest spaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
                "name",
                "description",
                area,
                CommonFixture.beSettingsRequest,
                "image");

        assertThat(getConstraintViolations(spaceCreateUpdateRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("공간 이미지에 빈 문자열이 들어오면 처리한다.")
    public void blankReservationMapImage(String image) {
        SpaceCreateUpdateRequest spaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
                "name",
                "description",
                "area",
                CommonFixture.beSettingsRequest,
                image);

        assertThat(getConstraintViolations(spaceCreateUpdateRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }
}
