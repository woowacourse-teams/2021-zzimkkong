package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Preset;
import com.woowacourse.zzimkkong.domain.Setting;
import com.woowacourse.zzimkkong.domain.TimeSlot;
import com.woowacourse.zzimkkong.dto.ErrorResponse;
import com.woowacourse.zzimkkong.dto.InputFieldErrorResponse;
import com.woowacourse.zzimkkong.dto.member.*;
import com.woowacourse.zzimkkong.dto.member.oauth.OauthMemberSaveRequest;
import com.woowacourse.zzimkkong.dto.space.EnabledDayOfWeekDto;
import com.woowacourse.zzimkkong.dto.space.SettingsRequest;
import com.woowacourse.zzimkkong.infrastructure.auth.AuthorizationExtractor;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static com.woowacourse.zzimkkong.Constants.*;
import static com.woowacourse.zzimkkong.DocumentUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

class MemberControllerTest extends AcceptanceTest {
    private Member pobi;
    private Setting setting;

    private SettingsRequest settingsRequest;
    private PresetCreateRequest presetCreateRequest;

    @BeforeEach
    void setUp() {
        pobi = new Member(EMAIL, passwordEncoder.encode(PW), ORGANIZATION);
        setting = Setting.builder()
                .availableTimeSlot(TimeSlot.of(
                        BE_AVAILABLE_START_TIME,
                        BE_AVAILABLE_END_TIME))
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build();

        settingsRequest = new SettingsRequest(
                BE_AVAILABLE_START_TIME,
                BE_AVAILABLE_END_TIME,
                BE_RESERVATION_TIME_UNIT.getMinutes(),
                BE_RESERVATION_MINIMUM_TIME_UNIT.getMinutes(),
                BE_RESERVATION_MAXIMUM_TIME_UNIT.getMinutes(),
                BE_RESERVATION_ENABLE,
                EnabledDayOfWeekDto.from(BE_ENABLED_DAY_OF_WEEK)
        );
        presetCreateRequest = new PresetCreateRequest(PRESET_NAME1, settingsRequest);
    }

    @Test
    @DisplayName("???????????? ???????????? ????????? ???????????? ?????? ????????? ????????????.")
    void join() {
        //given
        MemberSaveRequest newMemberSaveRequest = new MemberSaveRequest(NEW_EMAIL, PW, ORGANIZATION);

        // when
        ExtractableResponse<Response> response = saveMember(newMemberSaveRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"GOOGLE", "GITHUB"})
    @DisplayName("Oauth??? ????????? ??????????????????.")
    void joinByOauth(String oauth) {
        // given
        OauthMemberSaveRequest oauthMemberSaveRequest = new OauthMemberSaveRequest(NEW_EMAIL, ORGANIZATION, oauth);

        // when
        ExtractableResponse<Response> response = saveMemberByOauth(oauthMemberSaveRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("????????? ?????? ?????? ???, ???????????? ?????? ???????????? ???????????? ????????????.")
    void getMembers() {
        //given
        MemberSaveRequest newMemberSaveRequest = new MemberSaveRequest(NEW_EMAIL, PW, ORGANIZATION);
        saveMember(newMemberSaveRequest);

        // when
        String anotherEmail = "pobi@naver.com";
        ExtractableResponse<Response> response = validateDuplicateEmail(anotherEmail);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("???????????? ????????????.")
    void createPreset() {
        //given, when
        ExtractableResponse<Response> response = savePreset(presetCreateRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("????????? ?????? ???????????? ?????? ????????????.")
    void findAllPreset() {
        //given
        Preset firstPreset = new Preset(PRESET_NAME1, setting, pobi);
        Preset secondPreset = new Preset(PRESET_NAME2, setting, pobi);
        PresetCreateRequest presetCreateRequest2 = new PresetCreateRequest(PRESET_NAME2, settingsRequest);

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
    @DisplayName("???????????? ????????????.")
    void delete() {
        //given
        ExtractableResponse<Response> saveResponse = savePreset(presetCreateRequest);
        String api = saveResponse.header("location");

        //when
        ExtractableResponse<Response> response = deletePreset(api);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("????????? ????????? ????????? ????????? ??? ??????.")
    void findMe() {
        // given, when
        ExtractableResponse<Response> response = findMyInfo();

        MemberFindResponse actual = response.as(MemberFindResponse.class);
        MemberFindResponse expected = MemberFindResponse.from(pobi);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("????????? ????????? ????????? ????????? ??? ??????.")
    void updateMe() {
        // given
        MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest("woowabros");

        // when
        ExtractableResponse<Response> response = updateMyInfo(memberUpdateRequest);

        // then
        MemberFindResponse afterUpdate = findMyInfo().as(MemberFindResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(afterUpdate.getOrganization()).isEqualTo("woowabros");
    }

    @Test
    @DisplayName("????????? ?????? ????????? ??? ??????.")
    void deleteMe() {
        // given, when
        ExtractableResponse<Response> response = deleteMyInfo();
        ExtractableResponse<Response> errorExpectedResponse = findMyInfo();
        ErrorResponse errorResponse = errorExpectedResponse.as(InputFieldErrorResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(errorExpectedResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(errorResponse.getMessage()).isNotEmpty();
    }

    static ExtractableResponse<Response> saveMember(final MemberSaveRequest memberSaveRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("member/post", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(memberSaveRequest)
                .when().post("/api/managers")
                .then().log().all().extract();
    }

    static ExtractableResponse<Response> saveMemberByOauth(final OauthMemberSaveRequest oauthMemberSaveRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("member/post/oauth/" + oauthMemberSaveRequest.getOauthProvider(), getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(oauthMemberSaveRequest)
                .when().post("/api/managers/oauth")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> validateDuplicateEmail(final String email) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("member/get", getRequestPreprocessor(), getResponsePreprocessor()))
                .queryParam("email", email)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/managers")
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

    private ExtractableResponse<Response> findMyInfo() {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("member/myinfo/get", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/managers/me")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> updateMyInfo(MemberUpdateRequest memberUpdateRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("member/myinfo/put", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(memberUpdateRequest)
                .when().put("/api/managers/me")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> deleteMyInfo() {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("member/myinfo/delete", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/api/managers/me")
                .then().log().all().extract();
    }
}
