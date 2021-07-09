package com.woowacourse.zzimkkong.controller;

import com.woowacourse.zzimkkong.dto.ReservationDeleteRequest;
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
    public ReservationSaveRequest reservationSaveRequest = new ReservationSaveRequest(
            1L, //TODO: 나중에 인수테스트 전부 생기면 갖다 쓰기
            LocalDateTime.of(2021, 5, 6, 16, 23, 0),
            LocalDateTime.of(2021, 5, 6, 19, 23, 0),
            "회의입니다.", //todo 예약등록 API 파라미터인자 순서오류 해결되면 반영
            "sally",
            "2345"
    );

    @DisplayName("예약을 등록한다.")
    @Test
    void save() {
        //when
        ExtractableResponse<Response> response = saveReservation(reservationSaveRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("예약을 삭제한다.")
    @Test
    void delete() {
        //given
        saveReservation(reservationSaveRequest);
        ReservationDeleteRequest reservationDeleteRequest = new ReservationDeleteRequest("2345");

        //when
        final ExtractableResponse<Response> response = deleteReservation(reservationDeleteRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("잘못된 비밀번호로 예약삭제를 요청하면, 400 에러를 반환한다.")
    @Test
    void deleteWithWrongPassword() {
        //given
        saveReservation(reservationSaveRequest);
        ReservationDeleteRequest reservationDeleteRequest = new ReservationDeleteRequest("1234");

        //when
        final ExtractableResponse<Response> response = deleteReservation(reservationDeleteRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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

    private ExtractableResponse<Response> deleteReservation(final ReservationDeleteRequest reservationDeleteRequest) {
        return RestAssured
                .given(getRequestSpecification()).log().all()
                .accept("application/json")
                .filter(document("reservation/delete", getRequestPreprocessor(), getResponsePreprocessor()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reservationDeleteRequest)
                .when().delete("/api/maps/1/reservations/1")
                .then().log().all().extract();
    }
}
