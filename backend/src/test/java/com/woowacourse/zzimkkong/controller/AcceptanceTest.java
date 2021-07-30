package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.map.MapCreateUpdateRequest;
import com.woowacourse.zzimkkong.dto.member.MemberSaveRequest;
import com.woowacourse.zzimkkong.dto.space.SettingsRequest;
import com.woowacourse.zzimkkong.dto.space.SpaceCreateUpdateRequest;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalTime;

import static com.woowacourse.zzimkkong.CommonFixture.*;
import static com.woowacourse.zzimkkong.DocumentUtils.setRequestSpecification;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs
@ActiveProfiles("test")
public class AcceptanceTest {
    protected final MapCreateUpdateRequest mapCreateRequest = new MapCreateUpdateRequest(LUTHER.getName(), LUTHER.getMapDrawing(), LUTHER.getMapImageUrl());
    protected final MemberSaveRequest memberSaveRequest = new MemberSaveRequest(EMAIL, PASSWORD, ORGANIZATION);
    protected final SettingsRequest beSettingsRequest = new SettingsRequest(
            LocalTime.of(0, 0),
            LocalTime.of(23, 59),
            10,
            10,
            1440,
            true,
            null
    );

    protected final SpaceCreateUpdateRequest beSpaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
            "백엔드 강의실",
            "시니컬하네",
            "area",
            beSettingsRequest,
            "이미지 입니다"
    );

    protected final SettingsRequest feSettingsRequest = new SettingsRequest(
            LocalTime.of(0, 0),
            LocalTime.of(23, 59),
            10,
            10,
            1440,
            true,
            null
    );

    protected final SpaceCreateUpdateRequest feSpaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
            "프론트엔드 강의실1",
            "시니컬하네",
            "area",
            feSettingsRequest,
            "이미지 입니다"
    );

    protected final String SALLY_PASSWORD = "1230";
    protected final String SALLY_NAME = "샐리";
    protected final String SALLY_DESCRIPTION = "집 가고 싶은 회의";

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        RequestSpecification spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation))
                .build();
        setRequestSpecification(spec);
    }
}
