package com.woowacourse.zzimkkong.infrastructure.sharingid;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.ProfileEmoji;
import com.woowacourse.zzimkkong.domain.Space;
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
    private Space be;

    @Autowired
    SharingIdGenerator sharingIdGenerator;

    @Autowired
    Transcoder transcoder;

    @BeforeEach
    void setUp() {
        pobi = Member.builder()
                .id(1L)
                .email(EMAIL)
                .userName(POBI)
                .emoji(ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST)
                .password(PW)
                .organization(ORGANIZATION)
                .build();
        luther = new Map(1L,
                LUTHER_NAME,
                MAP_DRAWING_DATA,
                MAP_SVG,
                pobi);
        be = Space.builder()
                .id(1L)
                .name(BE_NAME)
                .map(luther)
                .area(SPACE_DRAWING)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .build();
    }

    @Test
    @DisplayName("Map 도메인 객체로부터 인코딩된 Sharing Id를 만들어낸다.")
    void generateSharingIdFromMap() {
        // given, when
        String sharingMapId = sharingIdGenerator.from(luther);

        // then
        assertThat(sharingMapId).isNotEmpty();
    }

    @Test
    @DisplayName("인코딩된 Sharing Id로부터 Id를 얻어낸다.")
    void parseIdFromEncodedString() {
        // given
        String sharingId = sharingIdGenerator.from(luther);

        // when
        Long actual = sharingIdGenerator.parseIdFrom(sharingId);
        Long expected = luther.getId();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("디코딩 할 수 없는 문자열이 Sharing Id로 주어지면 예외를 발생시킨다.")
    void parseIdFromInvalidToDecode() {
        // given
        String wrongSharingId = "zzimkkong";

        // when, then
        assertThatThrownBy(() -> sharingIdGenerator.parseIdFrom(wrongSharingId))
                .isInstanceOf(InvalidAccessLinkException.class);
    }

    @Test
    @DisplayName("Id 타입과 같은 Long 타입의 값으로 디코딩 할 수 없는 값이 Sharing Id로 주어지면 예외를 발생시킨다.")
    void parseIdFromNotIdValue() {
        // given
        String beforeEncoded = "zzimkkong";
        String encoded = transcoder.encode(beforeEncoded);

        // when, then
        assertThatThrownBy(() -> sharingIdGenerator.parseIdFrom(encoded))
                .isInstanceOf(InvalidAccessLinkException.class);
    }

    @Test
    @DisplayName("Space 도메인 객체로부터 인코딩된 Sharing Space Id를 만들어낸다.")
    void generateSharingIdFromSpace() {
        // given, when
        String sharingSpaceId = sharingIdGenerator.fromSpace(be);

        // then
        assertThat(sharingSpaceId).isNotEmpty();
    }

    @Test
    @DisplayName("인코딩된 Sharing Space Id로부터 Space Id를 얻어낸다.")
    void parseSpaceIdFromEncodedString() {
        // given
        String sharingSpaceId = sharingIdGenerator.fromSpace(be);

        // when
        Long actual = sharingIdGenerator.parseSpaceIdFrom(sharingSpaceId);
        Long expected = be.getId();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Map Sharing Id와 Space Sharing Id는 같은 DB Id여도 다른 값을 생성한다.")
    void mapAndSpaceSharingIdAreDifferent() {
        // given, when
        String sharingMapId = sharingIdGenerator.from(luther);
        String sharingSpaceId = sharingIdGenerator.fromSpace(be);

        // then (같은 id=1이지만 prefix가 다르므로 다른 값)
        assertThat(sharingMapId).isNotEqualTo(sharingSpaceId);
    }

    @Test
    @DisplayName("Map Sharing Id를 Space Id로 파싱하려고 하면 예외가 발생한다.")
    void parseMapSharingIdAsSpaceIdFails() {
        // given
        String sharingMapId = sharingIdGenerator.from(luther);

        // when, then
        assertThatThrownBy(() -> sharingIdGenerator.parseSpaceIdFrom(sharingMapId))
                .isInstanceOf(InvalidAccessLinkException.class);
    }

    @Test
    @DisplayName("디코딩 할 수 없는 문자열이 Space Sharing Id로 주어지면 예외를 발생시킨다.")
    void parseSpaceIdFromInvalidToDecode() {
        // given
        String wrongSharingId = "zzimkkong";

        // when, then
        assertThatThrownBy(() -> sharingIdGenerator.parseSpaceIdFrom(wrongSharingId))
                .isInstanceOf(InvalidAccessLinkException.class);
    }
}
