package com.woowacourse.zzimkkong.dto.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OAuthMemberSaveRequestTest {
    // todo Validation 테스트 작성

    private static final String BODY = "{\n" +
            "    \"email\": \"kkm97351@gmail.com\",\n" +
            "    \"organization\": \"woowacourse\",\n" +
            "    \"oauthProvider\": \"Github\"\n" +
            "}";

    @Test
    void objectMapper() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        OAuthMemberSaveRequest oAuthMemberSaveRequest = objectMapper.readValue(BODY, OAuthMemberSaveRequest.class);
        System.out.println("oAuthMemberSaveRequest = " + oAuthMemberSaveRequest);
    }
}
