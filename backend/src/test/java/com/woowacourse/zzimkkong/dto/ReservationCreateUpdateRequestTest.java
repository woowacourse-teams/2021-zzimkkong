package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.dto.reservation.ReservationCreateUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.*;
import static com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils.KST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationCreateUpdateRequestTest extends RequestTest {
    @ParameterizedTest
    @NullSource
    @DisplayName("예약 생성에 빈 dateTime이 들어오면 처리한다.")
    void blankDateTime(ZonedDateTime zonedDateTime) {
        ReservationCreateUpdateRequest startTime = new ReservationCreateUpdateRequest(
                zonedDateTime,
                LocalDateTime.now().atZone(KST.toZoneId()),
                "name",
                "description");

        ReservationCreateUpdateRequest endTime = new ReservationCreateUpdateRequest(
                LocalDateTime.now().atZone(KST.toZoneId()),
                zonedDateTime,
                "name",
                "description");

        assertThat(getConstraintViolations(startTime).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();

        assertThat(getConstraintViolations(endTime).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("예약 생성에 빈 이름이 들어오면 처리한다.")
    void blankName(String data) {
        ReservationCreateUpdateRequest nameRequest = new ReservationCreateUpdateRequest(
                LocalDateTime.now().atZone(KST.toZoneId()),
                LocalDateTime.now().atZone(KST.toZoneId()),
                data,
                "description");

        assertThat(getConstraintViolations(nameRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"hihellomorethantwenty:true", "hi~~~~:true", "hihello!:false"}, delimiter = ':')
    @DisplayName("예약 생성의 이름에 옳지 않은 형식의 문자열이 들어오면 처리한다.")
    void invalidName(String name, boolean flag) {
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                LocalDateTime.now().atZone(KST.toZoneId()),
                LocalDateTime.now().atZone(KST.toZoneId()),
                name,
                "description");

        assertThat(getConstraintViolations(reservationCreateUpdateRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(NAME_MESSAGE)))
                .isEqualTo(flag);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("예약 생성에 빈 이름이 들어오면 처리한다.")
    void blankDescription(String description) {
        ReservationCreateUpdateRequest descriptionRequest = new ReservationCreateUpdateRequest(
                LocalDateTime.now().atZone(KST.toZoneId()),
                LocalDateTime.now().atZone(KST.toZoneId()),
                "name",
                description);

        assertThat(getConstraintViolations(descriptionRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }

    @Test
    @DisplayName("예약 생성의 이름에 옳지 않은 형식의 문자열이 들어오면 처리한다.")
    void invalidDescription() {
        ReservationCreateUpdateRequest reservationCreateUpdateRequest = new ReservationCreateUpdateRequest(
                LocalDateTime.now().atZone(KST.toZoneId()),
                LocalDateTime.now().atZone(KST.toZoneId()),
                "name",
                "iamtenwordiamtenwordiamtenwordiamtenwordiamtenwordiamtenwordiamtenwordiamtenwordiamtenwordiamtenword1");

        assertThat(getConstraintViolations(reservationCreateUpdateRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(DESCRIPTION_MESSAGE)))
                .isTrue();
    }
}

