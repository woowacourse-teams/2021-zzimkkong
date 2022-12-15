package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.DatabaseCleaner;
import com.woowacourse.zzimkkong.domain.ProfileEmoji;
import com.woowacourse.zzimkkong.dto.map.MapCreateUpdateRequest;
import com.woowacourse.zzimkkong.dto.member.LoginRequest;
import com.woowacourse.zzimkkong.dto.member.MemberSaveRequest;
import com.woowacourse.zzimkkong.dto.space.EnabledDayOfWeekDto;
import com.woowacourse.zzimkkong.dto.space.SettingRequest;
import com.woowacourse.zzimkkong.dto.space.SpaceCreateUpdateRequest;
import com.woowacourse.zzimkkong.infrastructure.oauth.GithubRequester;
import com.woowacourse.zzimkkong.infrastructure.oauth.GoogleRequester;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static com.woowacourse.zzimkkong.Constants.*;
import static com.woowacourse.zzimkkong.DocumentUtils.setRequestSpecification;
import static com.woowacourse.zzimkkong.controller.AuthControllerTest.getToken;
import static com.woowacourse.zzimkkong.controller.MemberControllerTest.saveMember;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs
@ActiveProfiles("test")
class AcceptanceTest {
    protected static String accessToken;
    protected static final MemberSaveRequest memberSaveRequest = new MemberSaveRequest(EMAIL, USER_NAME, ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST, PW, ORGANIZATION);
    protected static final LoginRequest loginRequest = new LoginRequest(EMAIL, PW);
    protected final MapCreateUpdateRequest mapCreateUpdateRequest = new MapCreateUpdateRequest(LUTHER_NAME, MAP_DRAWING_DATA, MAP_SVG);
    protected final SettingRequest beSettingRequest = new SettingRequest(
            BE_AVAILABLE_START_TIME,
            BE_AVAILABLE_END_TIME,
            BE_RESERVATION_TIME_UNIT.getMinutes(),
            BE_RESERVATION_MINIMUM_TIME_UNIT.getMinutes(),
            BE_RESERVATION_MAXIMUM_TIME_UNIT.getMinutes(),
            EnabledDayOfWeekDto.from(BE_ENABLED_DAY_OF_WEEK)
    );
    protected final SpaceCreateUpdateRequest beSpaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
            BE_NAME,
            BE_COLOR,
            SPACE_DRAWING,
            MAP_SVG,
            BE_RESERVATION_ENABLE,
            List.of(beSettingRequest)
    );
    protected final SettingRequest feSettingRequest = new SettingRequest(
            FE_AVAILABLE_START_TIME,
            FE_AVAILABLE_END_TIME,
            FE_RESERVATION_TIME_UNIT.getMinutes(),
            FE_RESERVATION_MINIMUM_TIME_UNIT.getMinutes(),
            FE_RESERVATION_MAXIMUM_TIME_UNIT.getMinutes(),
            EnabledDayOfWeekDto.from(FE_ENABLED_DAY_OF_WEEK)
    );
    protected final SpaceCreateUpdateRequest feSpaceCreateUpdateRequest = new SpaceCreateUpdateRequest(
            FE_NAME,
            FE_COLOR,
            SPACE_DRAWING,
            MAP_SVG,
            FE_RESERVATION_ENABLE,
            List.of(feSettingRequest)
    );

    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @MockBean
    protected GithubRequester githubRequester;

    @MockBean
    protected GoogleRequester googleRequester;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        RequestSpecification spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation))
                .build();
        setRequestSpecification(spec);

        saveMember(memberSaveRequest);
        accessToken = getToken();
    }

    @AfterEach
    void deleteAll() {
        databaseCleaner.execute();
    }
}
