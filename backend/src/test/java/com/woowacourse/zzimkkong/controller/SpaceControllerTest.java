package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.space.SpaceFindResponse;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.woowacourse.zzimkkong.CommonFixture.*;
import static com.woowacourse.zzimkkong.DocumentUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class SpaceControllerTest extends AcceptanceTest {
    @Autowired
    private MemberRepository members;

    @Autowired
    private MapRepository maps;

    @Autowired
    private SpaceRepository spaces;

    @BeforeEach
    void setUp() {
        // todo API가 생성되면 repository를 사용하지 않고 테스트 데이터를 저장하기 -- 김김
        members.save(POBI);
        maps.save(LUTHER);
        spaces.save(BE);
    }

    @Test
    void find() {
        // when
        ExtractableResponse<Response> response = findSpace(LUTHER.getId(), BE.getId());
        SpaceFindResponse actual = response.body().as(SpaceFindResponse.class);
        SpaceFindResponse expected = SpaceFindResponse.from(BE);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    private ExtractableResponse<Response> findSpace(Long mapId, Long spaceId) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("space/get", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/maps/" + mapId.toString() + "/spaces/" + spaceId.toString())
                .then().log().all().extract();
    }
}


