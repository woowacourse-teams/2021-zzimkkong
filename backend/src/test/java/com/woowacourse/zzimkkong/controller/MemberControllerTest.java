package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Setting;
import com.woowacourse.zzimkkong.dto.PresetCreateRequest;
import com.woowacourse.zzimkkong.dto.member.MemberSaveRequest;
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

import static com.woowacourse.zzimkkong.Constants.*;
import static com.woowacourse.zzimkkong.DocumentUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

class MemberControllerTest extends AcceptanceTest {
    private Member pobi;
    private Setting setting;
    @BeforeEach
    void setUp() {
        pobi = new Member(EMAIL, PASSWORD, ORGANIZATION);
        setting = new Setting.Builder()
                .availableStartTime(BE_AVAILABLE_START_TIME)
                .availableEndTime(BE_AVAILABLE_END_TIME)
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build();
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
        //given
        SettingsRequest settingsRequest = new SettingsRequest(
                BE_AVAILABLE_START_TIME,
                BE_AVAILABLE_END_TIME,
                BE_RESERVATION_TIME_UNIT,
                BE_RESERVATION_MINIMUM_TIME_UNIT,
                BE_RESERVATION_MAXIMUM_TIME_UNIT,
                BE_RESERVATION_ENABLE,
                BE_ENABLED_DAY_OF_WEEK
        );

        PresetCreateRequest presetCreateRequest = new PresetCreateRequest(PRESET_NAME1, settingsRequest);

        //when
        ExtractableResponse<Response> response = savePreset(presetCreateRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
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

    private ExtractableResponse<Response> savePreset(final PresetCreateRequest presetCreateRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("preset/post", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(presetCreateRequest)
                .when().post("/api/members/presets")
                .then().log().all().extract();
    }
}
