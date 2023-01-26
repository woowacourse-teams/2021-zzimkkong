package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.ProfileEmoji;
import com.woowacourse.zzimkkong.dto.map.*;
import com.woowacourse.zzimkkong.infrastructure.auth.AuthorizationExtractor;
import com.woowacourse.zzimkkong.infrastructure.sharingid.SharingIdGenerator;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.woowacourse.zzimkkong.Constants.*;
import static com.woowacourse.zzimkkong.DocumentUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

class MapControllerTest extends AcceptanceTest {
    private Map luther;
    private Map smallHouse;
    private Member pobi;
    private String saveMapApi;
    private String createdMapApi;

    @Autowired
    SharingIdGenerator sharingIdGenerator;

    @BeforeEach
    void setUp() {
        saveMapApi = "/api/managers/maps";
        createdMapApi = saveMap(saveMapApi, mapCreateUpdateRequest).header("location");

        // For Test Comparison
        pobi = Member.builder()
                .email(EMAIL)
                .userName(POBI)
                .emoji(ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST)
                .password(passwordEncoder.encode(PW))
                .organization(ORGANIZATION)
                .build();
        luther = new Map(1L, LUTHER_NAME, MAP_DRAWING_DATA, MAP_SVG, pobi);
        smallHouse = new Map(2L, SMALL_HOUSE_NAME, MAP_DRAWING_DATA, MAP_SVG, pobi);

        luther.activateSharingMapId(sharingIdGenerator);
        smallHouse.activateSharingMapId(sharingIdGenerator);
    }

    @Test
    @DisplayName("특정 맵을 조회한다.")
    void find() {
        // given, when
        ExtractableResponse<Response> response = findMap(createdMapApi);
        MapFindResponse actualResponse = response.as(MapFindResponse.class);
        MapFindResponse expectedResponse = MapFindResponse.of(luther);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .ignoringFields("mapImageUrl")
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("특정 멤버가 가진 모든 맵을 조회한다.")
    void findAll() {
        // given
        ExtractableResponse<Response> smallHouseCreatedMapApi = saveMap(saveMapApi,
                new MapCreateUpdateRequest(smallHouse.getName(), smallHouse.getMapDrawing(), MAP_SVG));

        String lutherId = createdMapApi.split("/")[4];
        String smallHouseId = smallHouseCreatedMapApi.header("location").split("/")[4];
        Map lutherWithId = new Map(Long.parseLong(lutherId), luther.getName(), luther.getMapDrawing(), luther.getThumbnail(), luther.getMember());
        Map smallHouseWithId = new Map(Long.parseLong(smallHouseId), smallHouse.getName(), smallHouse.getMapDrawing(), smallHouse.getThumbnail(), smallHouse.getMember());
        Iterator<Map> expectedMapIterator = List.of(lutherWithId, smallHouseWithId).iterator();

        // when
        ExtractableResponse<Response> response = findAllMaps(saveMapApi);
        List<MapFindResponse> findMaps = response.as(MapFindAllResponse.class).getMaps();
        List<MapFindResponse> expected = Stream.of(luther, smallHouse)
                .map(MapFindResponse::of)
                .collect(Collectors.toList());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findMaps)
                .usingElementComparatorIgnoringFields("mapId", "mapImageUrl")
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("맵을 생성한다.")
    void create() {
        // given
        MapCreateUpdateRequest mapCreateUpdateRequest = new MapCreateUpdateRequest(smallHouse.getName(), smallHouse.getMapDrawing(), MAP_SVG);

        // when
        ExtractableResponse<Response> response = saveMap(saveMapApi, mapCreateUpdateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }


    @Test
    @DisplayName("맵을 수정한다.")
    void update() {
        // given
        MapCreateUpdateRequest mapCreateUpdateRequest = new MapCreateUpdateRequest("이름을 바꿔요", "맵의 메타데이터도 바꿔요", MAP_SVG);

        // when
        ExtractableResponse<Response> response = updateMap(createdMapApi, mapCreateUpdateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("맵을 삭제한다.")
    void delete() {
        // given, when
        ExtractableResponse<Response> response = deleteMap(createdMapApi);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("맵에 공지사항을 추가한다.")
    void saveNotice() {
        // given, when
        ExtractableResponse<Response> response = saveNotice(createdMapApi + "/notice", new NoticeCreateRequest("공지사항 입니다."));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("맵에 슬랙url을 추가한다.")
    void saveSlackUrl() {
        // given, when
        ExtractableResponse<Response> response = saveSlackUrl(createdMapApi + "/slack", new SlackCreateRequest("slack.url"));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("해당 맵의 슬랙url을 조회한다.")
    void findSlackUrl() {
        // given, when
        ExtractableResponse<Response> response = findSlackUrl(createdMapApi + "/slack");
        SlackFindResponse actualResponse = response.as(SlackFindResponse.class);
        SlackFindResponse expectedResponse = SlackFindResponse.from(luther);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }

    static ExtractableResponse<Response> saveMap(final String api, MapCreateUpdateRequest mapCreateUpdateRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("map/post", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(mapCreateUpdateRequest)
                .when().post(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> saveNotice(final String api, NoticeCreateRequest noticeCreateRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("map/noticePost", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(noticeCreateRequest)
                .when().post(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> saveSlackUrl(final String api, SlackCreateRequest slackCreateRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("map/slackPost", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(slackCreateRequest)
                .when().post(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findSlackUrl(final String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("map/slackGet", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(api)
                .then().log().all().extract();
    }

    static ExtractableResponse<Response> findMap(final String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("map/get", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findAllMaps(final String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("map/getAll", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> updateMap(final String api, MapCreateUpdateRequest mapCreateUpdateRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("map/put", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(mapCreateUpdateRequest)
                .when().put(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> deleteMap(final String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("map/delete", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(api)
                .then().log().all().extract();
    }
}
