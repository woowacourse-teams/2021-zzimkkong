package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Preset;
import com.woowacourse.zzimkkong.domain.Setting;
import com.woowacourse.zzimkkong.dto.member.MemberSaveRequest;
import com.woowacourse.zzimkkong.dto.member.PresetFindAllResponse;
import com.woowacourse.zzimkkong.dto.space.SettingsRequest;
import com.woowacourse.zzimkkong.infrastructure.AuthorizationExtractor;
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

class MemberControllerTest extends AcceptanceTest {
    private Preset bePreset;
    private Preset fePreset;
    private SettingsRequest beSettingsRequest;
    private SettingsRequest feSettingsRequest;

    @BeforeEach
    void setUp() {
        Member pobi = new Member(EMAIL, PASSWORD, ORGANIZATION);
        Setting beSetting = new Setting.Builder()
                .availableStartTime(BE_AVAILABLE_START_TIME)
                .availableEndTime(BE_AVAILABLE_END_TIME)
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build();

        Setting feSetting = new Setting.Builder()
                .availableStartTime(FE_AVAILABLE_START_TIME)
                .availableEndTime(FE_AVAILABLE_END_TIME)
                .reservationTimeUnit(FE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(FE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(FE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(FE_RESERVATION_ENABLE)
                .enabledDayOfWeek(FE_ENABLED_DAY_OF_WEEK)
                .build();

        beSettingsRequest = new SettingsRequest(
                BE_AVAILABLE_START_TIME,
                BE_AVAILABLE_END_TIME,
                BE_RESERVATION_TIME_UNIT,
                BE_RESERVATION_MINIMUM_TIME_UNIT,
                BE_RESERVATION_MAXIMUM_TIME_UNIT,
                BE_RESERVATION_ENABLE,
                BE_ENABLED_DAY_OF_WEEK
        );

        feSettingsRequest = new SettingsRequest(
                FE_AVAILABLE_START_TIME,
                FE_AVAILABLE_END_TIME,
                FE_RESERVATION_TIME_UNIT,
                FE_RESERVATION_MINIMUM_TIME_UNIT,
                FE_RESERVATION_MAXIMUM_TIME_UNIT,
                FE_RESERVATION_ENABLE,
                FE_ENABLED_DAY_OF_WEEK
        );

        bePreset = new Preset(beSetting, pobi);
        fePreset = new Preset(feSetting, pobi);
    }

    @Test
    @DisplayName("정상적인 회원가입 입력이 들어오면 회원 정보를 저장한다.")
    void join() {
        //given
        MemberSaveRequest newMemberSaveRequest = new MemberSaveRequest(NEW_EMAIL, PASSWORD, ORGANIZATION);

        // when
        ExtractableResponse<Response> response = saveMember(newMemberSaveRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("이메일 중복 확인 시, 중복되지 않은 이메일을 입력하면 통과한다.")
    void getMembers() {
        //given
        MemberSaveRequest newMemberSaveRequest = new MemberSaveRequest(NEW_EMAIL, PASSWORD, ORGANIZATION);
        saveMember(newMemberSaveRequest);

        // when
        String anotherEmail = "pobi@naver.com";
        ExtractableResponse<Response> response = validateDuplicateEmail(anotherEmail);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("프리셋을 저장한다.")
    void createPreset() {
        //given, when
        ExtractableResponse<Response> response = savePreset(beSettingsRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("멤버가 가진 프리셋을 모두 조회한다.")
    void findAllPreset() {
        //given
        savePreset(beSettingsRequest);
        savePreset(feSettingsRequest);

        //when
        ExtractableResponse<Response> response = findAllPresets();
        PresetFindAllResponse actual = response.as(PresetFindAllResponse.class);
        PresetFindAllResponse expected = PresetFindAllResponse.from(List.of(bePreset, fePreset));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    static ExtractableResponse<Response> saveMember(final MemberSaveRequest memberSaveRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("member/post", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(memberSaveRequest)
                .when().post("/api/members")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> validateDuplicateEmail(final String email) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("member/get", getRequestPreprocessor(), getResponsePreprocessor()))
                .queryParam("email", email)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/members")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> savePreset(final SettingsRequest settingsRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("preset/post", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(settingsRequest)
                .when().post("/api/members/presets")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findAllPresets() {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("preset/getAll", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/members/presets")
                .then().log().all().extract();
    }
}
