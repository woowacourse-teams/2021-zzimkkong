package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.dto.ReservationFindResponse;
import com.woowacourse.zzimkkong.dto.ReservationResponse;
import com.woowacourse.zzimkkong.dto.ReservationSaveRequest;
import com.woowacourse.zzimkkong.exception.NoSuchSpaceException;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static com.woowacourse.zzimkkong.controller.DocumentUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class ReservationControllerTest extends AcceptanceTest {
    @Autowired
    private SpaceRepository spaceRepository;

    private LocalDate targetDate;
    private Space be;

    @DisplayName("예약을 등록한다.")
    @Test
    void save() {
        //given
        ReservationSaveRequest reservationSaveRequest = new ReservationSaveRequest(
                1L, //TODO: 나중에 인수테스트 전부 생기면 갖다 쓰기
                LocalDateTime.of(2021, 5, 6, 16, 23, 0),
                LocalDateTime.of(2021, 5, 6, 19, 23, 0),
                "2345",
                "sally",
                "회의입니다."
        );

        //when
        ExtractableResponse<Response> response = saveReservation(reservationSaveRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("space id와 특정 날짜가 주어질 때, 해당 날짜에 속하는 space의 예약들만 찾아온다")
    @Test
    void find() {
        //given
        targetDate = LocalDate.of(2021, 7, 9);
        be = spaceRepository.findById(1L).orElseThrow(NoSuchSpaceException::new);

        //when
        ExtractableResponse<Response> response = findAllReservation(1L, "2021-07-09");
        ReservationFindResponse actualResponse = response.as(ReservationFindResponse.class);

        ReservationFindResponse expectedResponse = ReservationFindResponse.of(
                Arrays.asList(
                        new Reservation(
                                targetDate.atStartOfDay(),
                                targetDate.atTime(1, 0, 0),
                                "찜꽁 1차 회의",
                                    "찜꽁",
                                    "1234",
                                    be
                                ),
                        new Reservation(
                                targetDate.atTime(13, 0, 0),
                                targetDate.atTime(14, 0, 0),
                                "찜꽁 2차 회의",
                                "찜꽁",
                                "1234",
                                be
                        ),
                        new Reservation(
                                targetDate.atTime(18, 0, 0),
                                targetDate.atTime(23, 59, 59),
                                "찜꽁 3차 회의",
                                "찜꽁",
                                "6789",
                                be
                        )
                )
        );

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualResponse).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedResponse);
    }

    private ExtractableResponse<Response> saveReservation(final ReservationSaveRequest reservationSaveRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("reservation/post", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationSaveRequest)
                .when().post("/api/maps/1/reservations")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> findAllReservation(final Long spaceId, final String date) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .queryParams(
                        "spaceId", spaceId,
                        "date", date)
                .filter(document("reservation/get", getRequestPreprocessor(), getResponsePreprocessor()))
                .when().get("/api/maps/1/reservations")
                .then().log().all().extract();
    }
}
