package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.map.MapCreateRequest;
import com.woowacourse.zzimkkong.dto.map.MapFindAllResponse;
import com.woowacourse.zzimkkong.dto.map.MapFindResponse;
import com.woowacourse.zzimkkong.dto.member.LoginRequest;
import com.woowacourse.zzimkkong.dto.member.MemberSaveRequest;
import com.woowacourse.zzimkkong.dto.member.TokenResponse;
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
import static com.woowacourse.zzimkkong.controller.AuthControllerTest.login;
import static com.woowacourse.zzimkkong.controller.MemberControllerTest.saveMember;
import static com.woowacourse.zzimkkong.service.ServiceTestFixture.SMALL_HOUSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

class MapControllerTest extends AcceptanceTest {
    private String accessToken;

    @BeforeEach
    void setUp() {
        saveMember(new MemberSaveRequest(EMAIL, PASSWORD, ORGANIZATION));

        LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);
        ExtractableResponse<Response> response = login(loginRequest);

        TokenResponse tokenResponse = response.body().as(TokenResponse.class);
        accessToken = tokenResponse.getAccessToken();
    }

    @Test
    @DisplayName("특정 맵을 조회한다.")
    void find() {
        // given
        String api = saveMap("/api/managers/maps", new MapCreateRequest(LUTHER.getName(), LUTHER.getMapDrawing(), LUTHER.getMapImage()))
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
        saveMap("/api/managers/maps", new MapCreateRequest(LUTHER.getName(), LUTHER.getMapDrawing(), LUTHER.getMapImage()));
        saveMap("/api/managers/maps", new MapCreateRequest(SMALL_HOUSE.getName(), SMALL_HOUSE.getMapDrawing(), SMALL_HOUSE.getMapImage()));

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
        MapCreateRequest mapCreateRequest = new MapCreateRequest(LUTHER.getName(), LUTHER.getMapDrawing(), LUTHER.getMapImage());

        // when
        ExtractableResponse<Response> response = saveMap("/api/managers/maps", mapCreateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private ExtractableResponse<Response> saveMap(String api, MapCreateRequest mapCreateRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("map/post", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(mapCreateRequest)
                .when().post(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findMap(String api) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("map/get", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(api)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findAllMaps() {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .header("Authorization", AuthorizationExtractor.AUTHENTICATION_TYPE + " " + accessToken)
                .filter(document("map/getAll", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/managers/maps")
                .then().log().all().extract();
    }
}
