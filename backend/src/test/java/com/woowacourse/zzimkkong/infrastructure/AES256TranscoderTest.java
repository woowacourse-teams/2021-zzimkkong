package com.woowacourse.zzimkkong.infrastructure;

import com.woowacourse.zzimkkong.exception.infrastructure.DecodingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class AES256TranscoderTest {
    @Autowired
    private AES256Transcoder aes256Transcoder;

    @Test
    @DisplayName("문자열을 인코딩 할 수 있다.")
    void encode() {
        // given
        String testInput = "zzimkkong";

        // when
        String encoded = aes256Transcoder.encode(testInput);

        // then
        assertThat(encoded).isNotEmpty();
    }

    @Test
    @DisplayName("디코딩한 문자열은 인코딩 전의 문자열과 같다.")
    void decode() {
        // given
        String expected = "zzimkkong";
        String encoded = aes256Transcoder.encode(expected);

        // when
        String actual = aes256Transcoder.decode(encoded);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("디코딩 할 수 없는 문자열이 주어지면 에러를 발생시킨다.")
    void decodeFromInvalidString() {
        // given
        String wrongSharingId = "zzimkkong";

        // when, then
        assertThatThrownBy(() -> aes256Transcoder.decode(wrongSharingId))
                .isInstanceOf(DecodingException.class);
    }
}
