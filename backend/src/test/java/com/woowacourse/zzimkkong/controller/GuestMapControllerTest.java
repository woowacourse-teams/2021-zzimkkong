package com.woowacourse.zzimkkong.controller;

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
        createMemberResponse = saveMember(new MemberSaveRequest("pobi@email.com", "test1234", "woowacourse"));
        createMapResponse = saveMap("/api/managers/maps",
                new MapCreateUpdateRequest(
                        "루터회관",
                        "{'type': 'polyline', 'stroke': 'rgba(111, 111, 111, 1)', 'points': '['60, 250', '1, 231']'}",
                        "<?xml version='1.0'?><svg fill='#000000' xmlns='http://www.w3.org/2000/svg'  viewBox='0 0 30 30' width='30px' height='30px'>    <path d='M 7 4 C 6.744125 4 6.4879687 4.0974687 6.2929688 4.2929688 L 4.2929688 6.2929688 C 3.9019687 6.6839688 3.9019687 7.3170313 4.2929688 7.7070312 L 11.585938 15 L 4.2929688 22.292969 C 3.9019687 22.683969 3.9019687 23.317031 4.2929688 23.707031 L 6.2929688 25.707031 C 6.6839688 26.098031 7.3170313 26.098031 7.7070312 25.707031 L 15 18.414062 L 22.292969 25.707031 C 22.682969 26.098031 23.317031 26.098031 23.707031 25.707031 L 25.707031 23.707031 C 26.098031 23.316031 26.098031 22.682969 25.707031 22.292969 L 18.414062 15 L 25.707031 7.7070312 C 26.098031 7.3170312 26.098031 6.6829688 25.707031 6.2929688 L 23.707031 4.2929688 C 23.316031 3.9019687 22.682969 3.9019687 22.292969 4.2929688 L 15 11.585938 L 7.7070312 4.2929688 C 7.5115312 4.0974687 7.255875 4 7 4 z'/></svg>"
                )
        );
    }

    @Test
    @DisplayName("게스트는 publicId를 통해 Map을 조회할 수 있다.")
    void requestFindMapByPublicId() {
        // given
        String mapId = createMapResponse.header("Location").split("/")[4];

        ExtractableResponse<Response> mapFindResponseFromId = findMap("/api/managers/maps/" + mapId);
        MapFindResponse expected = mapFindResponseFromId.body().as(MapFindResponse.class);

        String publicMapId = expected.getPublicMapId();

        // when
        ExtractableResponse<Response> mapFindResponseFromPublicId = requestFindMapByPublicId(publicMapId);
        MapFindResponse actual = mapFindResponseFromPublicId.body().as(MapFindResponse.class);

        // then
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    private ExtractableResponse<Response> requestFindMapByPublicId(String publicMapId) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("map/getByPublicId", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/guests/maps?publicMapId=" + publicMapId)
                .then().log().all().extract();
    }
}
