package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.OauthProvider;
import com.woowacourse.zzimkkong.domain.Preset;
import com.woowacourse.zzimkkong.domain.Setting;
import com.woowacourse.zzimkkong.domain.oauth.GithubUserInfo;
import com.woowacourse.zzimkkong.domain.oauth.GoogleUserInfo;
import com.woowacourse.zzimkkong.dto.member.*;
import com.woowacourse.zzimkkong.dto.member.oauth.OauthMemberSaveRequest;
import com.woowacourse.zzimkkong.dto.member.oauth.OauthReadyResponse;
import com.woowacourse.zzimkkong.dto.space.SettingsRequest;
import com.woowacourse.zzimkkong.infrastructure.AuthorizationExtractor;
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
import java.util.Map;

import static com.woowacourse.zzimkkong.Constants.*;
import static com.woowacourse.zzimkkong.DocumentUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
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
                .availableStartTime(BE_AVAILABLE_START_TIME)
                .availableEndTime(BE_AVAILABLE_END_TIME)
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build();

        settingsRequest = new SettingsRequest(
                BE_AVAILABLE_START_TIME,
                BE_AVAILABLE_END_TIME,
                BE_RESERVATION_TIME_UNIT,
                BE_RESERVATION_MINIMUM_TIME_UNIT,
                BE_RESERVATION_MAXIMUM_TIME_UNIT,
                BE_RESERVATION_ENABLE,
                BE_ENABLED_DAY_OF_WEEK
        );
        presetCreateRequest = new PresetCreateRequest(PRESET_NAME1, settingsRequest);
    }

    @Test
    @DisplayName("정상적인 회원가입 입력이 들어오면 회원 정보를 저장한다.")
    void join() {
        //given
        MemberSaveRequest newMemberSaveRequest = new MemberSaveRequest(NEW_EMAIL, PW, ORGANIZATION);

        // when
        ExtractableResponse<Response> response = saveMember(newMemberSaveRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("Google Oauth 회원가입 입력이 들어오면 accessToken을 발급한다.")
    void getReadyToJoinByGoogleOauth() {
        // given
        given(googleRequester.supports(any(OauthProvider.class)))
                .willReturn(true);
        given(googleRequester.getUserInfoByCode(anyString()))
                .willReturn(new GoogleUserInfo(
                        "id",
                        NEW_EMAIL,
                        "verified_email",
                        "name",
                        "given_name",
                        "family_name",
                        "picture",
                        "locale"));

        OauthProvider oauthProvider = OauthProvider.GOOGLE;
        String code = "example-code";

        // when
        ExtractableResponse<Response> response = getReadyToJoin(oauthProvider, code);
        OauthReadyResponse expected = OauthReadyResponse.of(NEW_EMAIL, oauthProvider);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(OauthReadyResponse.class)).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Github Oauth 회원가입 입력이 들어오면 accessToken을 발급한다.")
    void getReadyToJoinByGithubOauth() {
        // given
        given(githubRequester.supports(any(OauthProvider.class)))
                .willReturn(true);
        given(githubRequester.getUserInfoByCode(anyString()))
                .willReturn(GithubUserInfo.from(Map.of("email", NEW_EMAIL)));

        OauthProvider oauthProvider = OauthProvider.GITHUB;
        String code = "example-code";

        // when
        ExtractableResponse<Response> response = getReadyToJoin(oauthProvider, code);
        OauthReadyResponse oauthReadyResponse = response.as(OauthReadyResponse.class);

        // then
        assertThat(oauthReadyResponse.getOauthProvider()).isEqualTo(oauthProvider);
        assertThat(oauthReadyResponse.getEmail()).isEqualTo(NEW_EMAIL);
    }

    @ParameterizedTest
    @ValueSource(strings = {"GOOGLE", "GITHUB"})
    @DisplayName("Oauth을 이용해 회원가입한다.")
    void joinByOauth(String oauth) {
        // given
        OauthMemberSaveRequest oauthMemberSaveRequest = new OauthMemberSaveRequest(NEW_EMAIL, ORGANIZATION, oauth);

        // when
        ExtractableResponse<Response> response = saveMemberByOauth(oauthMemberSaveRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("이메일 중복 확인 시, 중복되지 않은 이메일을 입력하면 통과한다.")
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

    static ExtractableResponse<Response> saveMember(final MemberSaveRequest memberSaveRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("member/post", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(memberSaveRequest)
                .when().post("/api/members/join")
                .then().log().all().extract();
    }

    static ExtractableResponse<Response> getReadyToJoin(final OauthProvider oauthProvider, final String code) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("member/get/oauth/" + oauthProvider.name(), getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/guests/" + oauthProvider + "?code=" + code)
                .then().log().all().extract();
    }

    static ExtractableResponse<Response> saveMemberByOauth(final OauthMemberSaveRequest oauthMemberSaveRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("member/post/oauth/" + oauthMemberSaveRequest.getOauthProvider(), getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(oauthMemberSaveRequest)
                .when().post("/api/guests/oauth")
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
