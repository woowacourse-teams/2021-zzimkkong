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
import static com.woowacourse.zzimkkong.service.ServiceTestFixture.SMALL_HOUSE;
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
        String api = saveMap("/api/managers/maps", new MapCreateUpdateRequest(LUTHER.getName(), LUTHER.getMapDrawing(), LUTHER.getMapImage()))
                .header("location");

        // when
        ExtractableResponse<Response> response = findMap(api);
        MapFindResponse actualResponse = response.as(MapFindResponse.class);
        MapFindResponse expectedResponse = MapFindResponse.from(LUTHER);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("특정 멤버가 가진 모든 맵을 조회한다.")
    void findAll() {
        // given
        saveMap("/api/managers/maps", new MapCreateUpdateRequest(LUTHER.getName(), LUTHER.getMapDrawing(), LUTHER.getMapImage()));
        saveMap("/api/managers/maps", new MapCreateUpdateRequest(SMALL_HOUSE.getName(), SMALL_HOUSE.getMapDrawing(), SMALL_HOUSE.getMapImage()));

        // when
        ExtractableResponse<Response> response = findAllMaps();
        MapFindAllResponse findMaps = response.as(MapFindAllResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findMaps).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(MapFindAllResponse.from(List.of(LUTHER, SMALL_HOUSE)));
    }

    @Test
    @DisplayName("맵을 생성한다.")
    void create() {
        // given
        MapCreateUpdateRequest mapCreateUpdateRequest = new MapCreateUpdateRequest(LUTHER.getName(), LUTHER.getMapDrawing(),
                "<svg xmlns='http://www.w3.org/2000/svg' height='24px' viewBox='0 0 24 24' width='24px' fill='#000000'><path d='M0 0h24v24H0V0z' fill='none'/><path d='M16.5 3c-1.74 0-3.41.81-4.5 2.09C10.91 3.81 9.24 3 7.5 3 4.42 3 2 5.42 2 8.5c0 3.78 3.4 6.86 8.55 11.54L12 21.35l1.45-1.32C18.6 15.36 22 12.28 22 8.5 22 5.42 19.58 3 16.5 3zm-4.4 15.55l-.1.1-.1-.1C7.14 14.24 4 11.39 4 8.5 4 6.5 5.5 5 7.5 5c1.54 0 3.04.99 3.57 2.36h1.87C13.46 5.99 14.96 5 16.5 5c2 0 3.5 1.5 3.5 3.5 0 2.89-3.14 5.74-7.9 10.05z'/></svg>");

        // when
        ExtractableResponse<Response> response = saveMap("/api/managers/maps", mapCreateUpdateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }


    @Test
    @DisplayName("맵을 수정한다.")
    void update() {
        // given
        String api = saveMap("/api/managers/maps", new MapCreateUpdateRequest(LUTHER.getName(), LUTHER.getMapDrawing(), LUTHER.getMapImage()))
                .header("location");
        MapCreateUpdateRequest mapCreateUpdateRequest = new MapCreateUpdateRequest("이름을 바꿔요", "맵의 메타데이터도 바꿔요", "이미지 url도 바꿔요");

        // when
        ExtractableResponse<Response> response = updateMap(api, mapCreateUpdateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
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
}
