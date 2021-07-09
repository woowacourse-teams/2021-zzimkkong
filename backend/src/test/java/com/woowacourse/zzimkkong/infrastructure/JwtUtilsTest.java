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
    @Autowired
    private JwtUtils jwtUtils;

    @DisplayName("Payload를 담아 token을 발급한다.")
    @Test
    public void createToken() {
        // given
        Map<String, Object> payload = JwtUtils.payloadBuilder()
                .setSubject("pobi@woowa.com")
                .build();

        // when
        String token = jwtUtils.createToken(payload);

        // then
        assertThat(token).isInstanceOf(String.class);
    }
}
