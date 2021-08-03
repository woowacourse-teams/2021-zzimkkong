package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.dto.map.MapCreateUpdateRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.EMPTY_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;

class MapCreateUpdateRequestTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("맵 이름에 빈 문자열이 들어오면 처리한다.")
    public void blankMapName(String name) {
        MapCreateUpdateRequest mapCreateUpdateRequest = new MapCreateUpdateRequest(name, "drawing", "image");
        Set<ConstraintViolation<MapCreateUpdateRequest>> violations = validator.validate(mapCreateUpdateRequest);
        assertThat(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("맵 그림 요소에 빈 문자열이 들어오면 처리한다.")
    public void blankMapDrawing(String drawing) {
        MapCreateUpdateRequest mapCreateUpdateRequest = new MapCreateUpdateRequest("name", drawing, "image");
        Set<ConstraintViolation<MapCreateUpdateRequest>> violations = validator.validate(mapCreateUpdateRequest);
        assertThat(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("맵 이미지에 빈 문자열이 들어오면 처리한다.")
    public void blankMapImage(String image) {
        MapCreateUpdateRequest mapCreateUpdateRequest = new MapCreateUpdateRequest("name", "drawing", image);
        Set<ConstraintViolation<MapCreateUpdateRequest>> violations = validator.validate(mapCreateUpdateRequest);
        assertThat(violations.stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }
}
