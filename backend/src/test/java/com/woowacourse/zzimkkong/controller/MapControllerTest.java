package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.map.MapCreateUpdateRequest;
import com.woowacourse.zzimkkong.dto.map.MapFindAllResponse;
import com.woowacourse.zzimkkong.dto.map.MapFindResponse;
import com.woowacourse.zzimkkong.dto.member.MemberSaveRequest;
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

import static com.woowacourse.zzimkkong.CommonFixture.*;
import static com.woowacourse.zzimkkong.DocumentUtils.*;
import static com.woowacourse.zzimkkong.controller.AuthControllerTest.getToken;
import static com.woowacourse.zzimkkong.controller.MemberControllerTest.saveMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

class MapControllerTest extends AcceptanceTest {
    @BeforeEach
    void setUp() {
        saveMember(new MemberSaveRequest(EMAIL, PASSWORD, ORGANIZATION));
    }

    @Test
    @DisplayName("특정 맵을 조회한다.")
    void find() {
        // given
        String api = saveMap("/api/managers/maps", new MapCreateUpdateRequest(LUTHER.getName(), LUTHER.getMapDrawing(), MAP_SVG))
                .header("location");

        // when
        ExtractableResponse<Response> response = findMap(api);
        MapFindResponse actualResponse = response.as(MapFindResponse.class);
        MapFindResponse expectedResponse = MapFindResponse.from(LUTHER);

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
        saveMap("/api/managers/maps", new MapCreateUpdateRequest(LUTHER.getName(), LUTHER.getMapDrawing(), MAP_SVG));
        saveMap("/api/managers/maps", new MapCreateUpdateRequest(SMALL_HOUSE.getName(), SMALL_HOUSE.getMapDrawing(), MAP_SVG));

        // when
        ExtractableResponse<Response> response = findAllMaps();
        List<MapFindResponse> findMaps = response.as(MapFindAllResponse.class).getMaps();
        List<MapFindResponse> expected = MapFindAllResponse.from(List.of(LUTHER, SMALL_HOUSE)).getMaps();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findMaps)
                .usingElementComparatorIgnoringFields("mapImageUrl")
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("맵을 생성한다.")
    void create() {
        // given
        MapCreateUpdateRequest mapCreateUpdateRequest = new MapCreateUpdateRequest(LUTHER.getName(), LUTHER.getMapDrawing(), MAP_SVG);

        // when
        ExtractableResponse<Response> response = saveMap("/api/managers/maps", mapCreateUpdateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }


    @Test
    @DisplayName("맵을 수정한다.")
    void update() {
        // given
        String api = saveMap("/api/managers/maps", new MapCreateUpdateRequest(LUTHER.getName(), LUTHER.getMapDrawing(), MAP_SVG))
                .header("location");
        MapCreateUpdateRequest mapCreateUpdateRequest = new MapCreateUpdateRequest("이름을 바꿔요", "맵의 메타데이터도 바꿔요", MAP_SVG);

        // when
        ExtractableResponse<Response> response = updateMap(api, mapCreateUpdateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("맵을 삭제한다.")
    void delete() {
        // given
        String api = saveMap("/api/managers/maps", new MapCreateUpdateRequest(LUTHER.getName(), LUTHER.getMapDrawing(), MAP_SVG))
                .header("location");

        // when
        ExtractableResponse<Response> response = deleteMap(api);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    static ExtractableResponse<Response> saveMap(String api, MapCreateUpdateRequest mapCreateUpdateRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + getToken())
                .filter(document("map/post", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(mapCreateUpdateRequest)
                .when().post(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findMap(String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + getToken())
                .filter(document("map/get", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findAllMaps() {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + getToken())
                .filter(document("map/getAll", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/managers/maps")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> updateMap(String api, MapCreateUpdateRequest mapCreateUpdateRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + getToken())
                .filter(document("map/put", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(mapCreateUpdateRequest)
                .when().put(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> deleteMap(String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + getToken())
                .filter(document("map/delete", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(api)
                .then().log().all().extract();
    }
}
