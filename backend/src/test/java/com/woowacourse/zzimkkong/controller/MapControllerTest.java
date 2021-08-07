package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.map.MapCreateUpdateRequest;
import com.woowacourse.zzimkkong.dto.map.MapFindAllResponse;
import com.woowacourse.zzimkkong.dto.map.MapFindResponse;
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

class MapControllerTest extends AcceptanceTest {
    private Map luther;
    private Map smallHouse;
    private Member pobi;
    private String saveMapApi;
    private String createdMapApi;

    @BeforeEach
    void setUp() {
        saveMapApi = "/api/managers/maps";
        createdMapApi = saveMap(saveMapApi, mapCreateUpdateRequest).header("location");

        // For Test Comparison
        pobi = new Member(EMAIL, PASSWORD, ORGANIZATION);
        luther = new Map(LUTHER_NAME, MAP_DRAWING_DATA, MAP_IMAGE_URL, pobi);
        smallHouse = new Map(SMALL_HOUSE_NAME, MAP_DRAWING_DATA, MAP_IMAGE_URL, pobi);
    }

    @Test
    @DisplayName("특정 맵을 조회한다.")
    void find() {
        // given, when
        ExtractableResponse<Response> response = findMap(createdMapApi);
        MapFindResponse actualResponse = response.as(MapFindResponse.class);
        MapFindResponse expectedResponse = MapFindResponse.from(luther);

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
        saveMap(

                saveMapApi,
                new MapCreateUpdateRequest(smallHouse.getName(), smallHouse.getMapDrawing(), MAP_SVG));

        // when
        ExtractableResponse<Response> response = findAllMaps(saveMapApi);
        List<MapFindResponse> findMaps = response.as(MapFindAllResponse.class).getMaps();
        List<MapFindResponse> expected = MapFindAllResponse.of(List.of(luther, smallHouse), pobi).getMaps();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findMaps)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .ignoringFields("mapImageUrl")
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

    private ExtractableResponse<Response> findMap(final String api) {
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
