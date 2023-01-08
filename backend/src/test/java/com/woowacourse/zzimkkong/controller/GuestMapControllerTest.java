package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.ProfileEmoji;
import com.woowacourse.zzimkkong.dto.map.MapCreateUpdateRequest;
import com.woowacourse.zzimkkong.dto.map.MapFindResponse;
import com.woowacourse.zzimkkong.dto.member.MemberSaveRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static com.woowacourse.zzimkkong.Constants.*;
import static com.woowacourse.zzimkkong.DocumentUtils.*;
import static com.woowacourse.zzimkkong.controller.MapControllerTest.findMap;
import static com.woowacourse.zzimkkong.controller.MapControllerTest.saveMap;
import static com.woowacourse.zzimkkong.controller.MemberControllerTest.saveMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

class GuestMapControllerTest extends AcceptanceTest {
    private ExtractableResponse<Response> createMemberResponse;
    private ExtractableResponse<Response> createMapResponse;

    @BeforeEach
    void setUp() {
        createMemberResponse = saveMember(new MemberSaveRequest(EMAIL, POBI, ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST, PW));
        createMapResponse = saveMap("/api/managers/maps",
                new MapCreateUpdateRequest(
                        LUTHER_NAME,
                        MAP_DRAWING_DATA,
                        MAP_SVG
                )
        );
    }

    @Test
    @DisplayName("게스트는 Sharing Id를 통해 Map을 조회할 수 있다.")
    void requestFindMapBySharingId() {
        // given
        String mapId = createMapResponse.header("Location").split("/")[4];

        ExtractableResponse<Response> mapFindResponseFromId = findMap("/api/managers/maps/" + mapId);
        MapFindResponse expected = mapFindResponseFromId.body().as(MapFindResponse.class);
        String sharingId = expected.getSharingMapId();

        // when
        ExtractableResponse<Response> mapFindResponseFromSharingId = requestFindMapBySharingId(sharingId);
        MapFindResponse actual = mapFindResponseFromSharingId.body().as(MapFindResponse.class);

        // then
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    private ExtractableResponse<Response> requestFindMapBySharingId(String sharingMapId) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("map/getBySharingId", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/guests/maps?sharingMapId=" + sharingMapId)
                .then().log().all().extract();
    }
}
