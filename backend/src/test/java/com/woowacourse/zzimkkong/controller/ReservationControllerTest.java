package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.ReservationSaveRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static com.woowacourse.zzimkkong.controller.DocumentUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class ReservationControllerTest extends AcceptanceTest {
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
}
