package com.woowacourse.zzimkkong.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.zzimkkong.exception.InvalidTokenException;
import com.woowacourse.zzimkkong.exception.TokenExpiredException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class JwtUtilsTest {
    private static final String EMAIL = "pobi@email.com";
    private static final int INDEX_OF_HEADER = 0;
    private static final int INDEX_OF_PAYLOAD = 1;
    private static final int INDEX_OF_SIGNATURE = 2;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("Payload를 담아 token을 발급한다.")
    @Test
    void createToken() {
        // given
        Map<String, Object> payload = createPayload();

        // when
        String token = jwtUtils.createToken(payload);

        // then
        assertThat(token).isInstanceOf(String.class);
    }

    @DisplayName("발급한 토큰의 유효성을 검사한다.")
    @Test
    void validateToken() {
        // given
        Map<String, Object> payload = createPayload();

        String token = jwtUtils.createToken(payload);

        // when, then
        jwtUtils.validateToken(token);
    }

    @DisplayName("유효기간이 지나면 예외를 발생시킨다.")
    @Test
    void expiredTokenThrowsException() {
        // given
        JwtUtils jwtUtils = new JwtUtils("Temporal_Secret_Key", 0);

        Map<String, Object> payload = createPayload();

        String token = jwtUtils.createToken(payload);

        // when, then
        assertThatThrownBy(() -> jwtUtils.validateToken(token))
                .isInstanceOf(TokenExpiredException.class);
    }

    @DisplayName("조작된 토큰을 보내면 예외를 발생시킨다.")
    @Test
    void invalidTokenThrowsException() throws JsonProcessingException {
        // given
        Map<String, Object> payloadOrigin = createPayload();
        String token = jwtUtils.createToken(payloadOrigin);

        // when
        String manipulatedToken = extendExpirationDate(token);

        // then
        assertThatThrownBy(() -> jwtUtils.validateToken(manipulatedToken))
                .isInstanceOf(InvalidTokenException.class);
    }

    private Map<String, Object> createPayload() {
        return JwtUtils.payloadBuilder()
                .setSubject(EMAIL)
                .build();
    }

    private String extendExpirationDate(String token) throws JsonProcessingException {
        Map<String, Object> payload = extractPayload(token);

        payload.merge("exp", 1000, (oldValue, value) -> (Integer) oldValue + (Integer) value);

        String manipulatedPayload = encode(payload);

        String[] originJwt = token.split("\\.");
        List<String> manipulatedJwt = Arrays.asList(originJwt[INDEX_OF_HEADER], manipulatedPayload, originJwt[INDEX_OF_SIGNATURE]);

        return String.join("\\.", manipulatedJwt);
    }

    private Map<String, Object> extractPayload(String token) throws JsonProcessingException {
        String encodedPayload = token.split("\\.")[INDEX_OF_PAYLOAD];

        Decoder decoder = Base64.getDecoder();
        String decodedPayload = new String(decoder.decode(encodedPayload));

        return objectMapper.readValue(decodedPayload, Map.class);
    }

    private String encode(Map<String, Object> claims) throws JsonProcessingException {
        String serialized = objectMapper.writeValueAsString(claims);

        Encoder encoder = Base64.getEncoder();

        return new String(encoder.encode(serialized.getBytes()));
    }
}
