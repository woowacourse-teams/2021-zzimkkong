package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.dto.ErrorResponse;
import com.woowacourse.zzimkkong.dto.InputFieldErrorResponse;
import com.woowacourse.zzimkkong.dto.member.*;
import com.woowacourse.zzimkkong.dto.member.oauth.OauthMemberSaveRequest;
import com.woowacourse.zzimkkong.dto.space.EnabledDayOfWeekDto;
import com.woowacourse.zzimkkong.dto.space.SettingRequest;
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

    private SettingRequest settingRequest;
    private PresetCreateRequest presetCreateRequest;

    @BeforeEach
    void setUp() {
        pobi = Member.builder()
                .email(EMAIL)
                .userName(USER_NAME)
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
    @DisplayName("정상적인 회원가입 입력이 들어오면 회원 정보를 저장한다.")
    void join() {
        //given
        MemberSaveRequest newMemberSaveRequest = new MemberSaveRequest(NEW_EMAIL, NEW_USER_NAME, ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST, PW, ORGANIZATION);

        // when
        ExtractableResponse<Response> response = saveMember(newMemberSaveRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"GOOGLE", "GITHUB"})
    @DisplayName("Oauth을 이용해 회원가입한다.")
    void joinByOauth(String oauth) {
        // given
        OauthMemberSaveRequest oauthMemberSaveRequest = new OauthMemberSaveRequest(NEW_EMAIL, NEW_USER_NAME, ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST, ORGANIZATION, oauth);

        // when
        ExtractableResponse<Response> response = saveMemberByOauth(oauthMemberSaveRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("이메일 중복 확인 시, 중복되지 않은 이메일을 입력하면 통과한다.")
    void getMembers() {
        //given
        MemberSaveRequest newMemberSaveRequest = new MemberSaveRequest(NEW_EMAIL, USER_NAME, ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST, PW, ORGANIZATION);
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

    @Test
    @DisplayName("유저는 자신의 정보를 조회할 수 있다.")
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
    @DisplayName("유저는 자신의 정보를 수정할 수 있다.")
    void updateMe() {
        // given
        MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest("woowabros", "sakjung", ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST);

        // when
        ExtractableResponse<Response> response = updateMyInfo(memberUpdateRequest);

        // then
        MemberFindResponse afterUpdate = findMyInfo().as(MemberFindResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(afterUpdate.getOrganization()).isEqualTo("woowabros");
        assertThat(afterUpdate.getUserName()).isEqualTo("sakjung");
    }

    @Test
    @DisplayName("유저는 회원 탈퇴할 수 있다.")
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

    @Test
    @DisplayName("프로필 이미지로 사용할 수 있는 이모지 리스트를 조회할 수 있다.")
    void findEmojis() {
        // given, when
        ExtractableResponse<Response> response = findProfileEmojis();

        ProfileEmojisResponse actual = response.as(ProfileEmojisResponse.class);
        ProfileEmojisResponse expected = ProfileEmojisResponse.from(List.of(ProfileEmoji.values()));

        // then
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

    private ExtractableResponse<Response> findProfileEmojis() {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("member/get/emojis", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/managers/emojis")
                .then().log().all().extract();
    }
}
