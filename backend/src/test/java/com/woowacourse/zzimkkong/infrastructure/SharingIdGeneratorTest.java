package com.woowacourse.zzimkkong.infrastructure;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.exception.map.InvalidAccessLinkException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class SharingIdGeneratorTest {
    private Member pobi;
    private Map luther;

    @Autowired
    SharingIdGenerator sharingIdGenerator;

    @Autowired
    Transcoder transcoder;

    @BeforeEach
    void setUp() {
        pobi = new Member(1L, EMAIL, PASSWORD, ORGANIZATION);
        luther = new Map(1L,
                LUTHER_NAME,
                MAP_DRAWING_DATA,
                MAP_IMAGE_URL,
                pobi);
    }

    @Test
    @DisplayName("Map 도메인 객체로부터 인코딩된 publicId를 만들어낸다.")
    void generatePublicIdFromMap() {
        // given, when
        String publicMapId = sharingIdGenerator.from(luther);

        // then
        assertThat(publicMapId).isNotEmpty();
    }

    @Test
    @DisplayName("인코딩된 publicMapId로부터 Id를 얻어낸다.")
    void parseIdFromEncodedString() {
        // given
        String publicMapId = sharingIdGenerator.from(luther);

        // when
        Long actual = sharingIdGenerator.parseIdFrom(publicMapId);
        Long expected = luther.getId();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("디코딩 할 수 없는 문자열이 publicId로 주어지면 예외를 발생시킨다.")
    void parseIdFromInvalidToDecode() {
        // given
        String wrongPublicId = "zzimkkong";

        // when, then
        assertThatThrownBy(() -> sharingIdGenerator.parseIdFrom(wrongPublicId))
                .isInstanceOf(InvalidAccessLinkException.class);
    }

    @Test
    @DisplayName("Id 타입과 같은 Long 타입의 값으로 디코딩 할 수 없는 값이 publicId로 주어지면 예외를 발생시킨다.")
    void parseIdFromNotIdValue() {
        // given
        String beforeEncoded = "zzimkkong";
        String encoded = transcoder.encode(beforeEncoded);

        // when, then
        assertThatThrownBy(() -> sharingIdGenerator.parseIdFrom(encoded))
                .isInstanceOf(InvalidAccessLinkException.class);
    }
}
