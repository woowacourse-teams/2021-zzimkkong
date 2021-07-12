package com.woowacourse.zzimkkong.infrastructure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class JwtUtilsTest {
    private static final String EMAIL = "pobi@email.com";

    @Autowired
    private JwtUtils jwtUtils;

    @DisplayName("Payload를 담아 token을 발급한다.")
    @Test
    void createToken() {
        // given
        Map<String, Object> payload = JwtUtils.payloadBuilder()
                .setSubject(EMAIL)
                .build();

        // when
        String token = jwtUtils.createToken(payload);

        // then
        assertThat(token).isInstanceOf(String.class);
    }

    @DisplayName("발급한 토큰의 유효성을 검사한다.")
    @Test
    void validateToken() {
        // given
        Map<String, Object> payload = JwtUtils.payloadBuilder()
                .setSubject(EMAIL)
                .build();

        String token = jwtUtils.createToken(payload);

        // when, then
        jwtUtils.validateToken(token);
    }
}
