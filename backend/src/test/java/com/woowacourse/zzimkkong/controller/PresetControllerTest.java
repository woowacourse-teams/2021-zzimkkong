package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.member.PresetCreateRequest;
import com.woowacourse.zzimkkong.dto.member.PresetFindAllResponse;
import com.woowacourse.zzimkkong.dto.space.EnabledDayOfWeekDto;
import com.woowacourse.zzimkkong.dto.space.SettingRequest;
import com.woowacourse.zzimkkong.infrastructure.auth.AuthorizationExtractor;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static com.woowacourse.zzimkkong.Constants.*;
import static com.woowacourse.zzimkkong.DocumentUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

class PresetControllerTest extends AcceptanceTest {
    private Member pobi;
    private Setting setting;

    private SettingRequest settingRequest;
    private PresetCreateRequest presetCreateRequest;

    @BeforeEach
    void setUp() {
        pobi = Member.builder()
                .email(EMAIL)
                .userName(POBI)
                .emoji(ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST)
                .password(passwordEncoder.encode(PW))
                .organization(ORGANIZATION)
                .build();
        setting = Setting.builder()
                .settingTimeSlot(TimeSlot.of(
                        BE_AVAILABLE_START_TIME,
                        BE_AVAILABLE_END_TIME))
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build();

        settingRequest = new SettingRequest(
                BE_AVAILABLE_START_TIME,
                BE_AVAILABLE_END_TIME,
                BE_RESERVATION_TIME_UNIT.getMinutes(),
                BE_RESERVATION_MINIMUM_TIME_UNIT.getMinutes(),
                BE_RESERVATION_MAXIMUM_TIME_UNIT.getMinutes(),
                EnabledDayOfWeekDto.from(BE_ENABLED_DAY_OF_WEEK)
        );
        presetCreateRequest = new PresetCreateRequest(PRESET_NAME1, settingRequest);
    }

    @Test
    @DisplayName("프리셋을 저장한다.")
    void createPreset() {
        //given, when
        ExtractableResponse<Response> response = savePreset(presetCreateRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("멤버가 가진 프리셋을 모두 조회한다.")
    void findAllPreset() {
        //given
        Preset firstPreset = Preset.builder()
                .name(PRESET_NAME1)
                .settingTimeSlot(setting.getSettingTimeSlot())
                .reservationTimeUnit(setting.getReservationTimeUnit())
                .reservationMinimumTimeUnit(setting.getReservationMinimumTimeUnit())
                .reservationMaximumTimeUnit(setting.getReservationMaximumTimeUnit())
                .enabledDayOfWeek(setting.getEnabledDayOfWeek())
                .member(pobi)
                .build();
        Preset secondPreset = Preset.builder()
                .name(PRESET_NAME2)
                .settingTimeSlot(setting.getSettingTimeSlot())
                .reservationTimeUnit(setting.getReservationTimeUnit())
                .reservationMinimumTimeUnit(setting.getReservationMinimumTimeUnit())
                .reservationMaximumTimeUnit(setting.getReservationMaximumTimeUnit())
                .enabledDayOfWeek(setting.getEnabledDayOfWeek())
                .member(pobi)
                .build();
        PresetCreateRequest presetCreateRequest2 = new PresetCreateRequest(PRESET_NAME2, settingRequest);

        savePreset(presetCreateRequest);
        savePreset(presetCreateRequest2);

        //when
        ExtractableResponse<Response> response = findAllPresets();
        PresetFindAllResponse actual = response.as(PresetFindAllResponse.class);
        PresetFindAllResponse expected = PresetFindAllResponse.from(List.of(firstPreset, secondPreset));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expected);
    }

    @Test
    @DisplayName("프리셋을 삭제한다.")
    void delete() {
        //given
        ExtractableResponse<Response> saveResponse = savePreset(presetCreateRequest);
        String api = saveResponse.header("location");

        //when
        ExtractableResponse<Response> response = deletePreset(api);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> savePreset(final PresetCreateRequest presetCreateRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("preset/post", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(presetCreateRequest)
                .when().post("/api/managers/presets")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findAllPresets() {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("preset/getAll", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/managers/presets")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> deletePreset(String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("preset/delete", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(api)
                .then().log().all().extract();
    }
}