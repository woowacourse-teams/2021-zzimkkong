package com.woowacourse.zzimkkong.infrastructure;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.exception.infrastructure.DecodingException;
import com.woowacourse.zzimkkong.exception.map.InvalidAccessLinkException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class PublicIdGeneratorTest {
    // todo 테스트 fixture 리팩토링이 끝난 후 Constants 클래스 활용하기
    private final static Member MEMBER = new Member(1L, "pobi@woowa.com", "test1234", "woowacourse");
    private final static Map MAP = new Map(1L,
            "루터회관 14층",
            "{'type': 'polyline', 'stroke': 'rgba(111, 111, 111, 1)', 'points': '['60, 250', '1, 231']'}",
            "https://zzimkkong-personal.s3.ap-northeast-2.amazonaws.com/thumbnails/2387563.png",
            MEMBER);

    @Autowired
    PublicIdGenerator publicIdGenerator;

    @Autowired
    Transcoder transcoder;

    @Test
    @DisplayName("Map 도메인 객체로부터 인코딩된 publicId를 만들어낸다.")
    void generatePublicIdFromMap() {
        // given, when
        String publicMapId = publicIdGenerator.from(MAP);

        // then
        assertThat(publicMapId).isNotEmpty();
    }

    @Test
    @DisplayName("인코딩된 publicMapId로부터 Id를 얻어낸다.")
    void parseIdFromEncodedString() {
        // given
        String publicMapId = publicIdGenerator.from(MAP);

        // when
        Long actual = publicIdGenerator.parseIdFrom(publicMapId);
        Long expected = MAP.getId();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("디코딩 할 수 없는 문자열이 publicId로 주어지면 예외를 발생시킨다.")
    void parseIdFromInvalidToDecode() {
        // given
        String wrongPublicId = "zzimkkong";

        // when, then
        assertThatThrownBy(() -> publicIdGenerator.parseIdFrom(wrongPublicId))
                .isInstanceOf(InvalidAccessLinkException.class);
    }

    @Test
    @DisplayName("Id 타입과 같은 Long 타입의 값으로 디코딩 할 수 없는 값이 publicId로 주어지면 예외를 발생시킨다.")
    void parseIdFromNotIdValue() {
        // given
        String beforeEncoded = "zzimkkong";
        String encoded = transcoder.encode(beforeEncoded);

        // when, then
        assertThatThrownBy(() -> publicIdGenerator.parseIdFrom(encoded))
                .isInstanceOf(InvalidAccessLinkException.class);
    }
}
