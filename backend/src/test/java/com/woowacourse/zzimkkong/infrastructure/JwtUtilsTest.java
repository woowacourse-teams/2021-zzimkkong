package com.woowacourse.zzimkkong.infrastructure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class JwtUtilsTest {
    @Autowired
    private JwtUtils jwtUtils;

    @DisplayName("Map 타입의 객체를 받아 token을 발급한다.")
    @Test
    public void createToken() {
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("sub", "pobi@woowa.com");

        String token = jwtUtils.createToken(payload);

        assertThat(token).isInstanceOf(String.class);
    }
}
