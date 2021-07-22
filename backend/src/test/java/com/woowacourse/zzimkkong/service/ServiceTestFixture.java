package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ServiceTestFixture {
    public static final String EMAIL = "pobi@email.com";
    public static final String PASSWORD = "test1234";
    public static final String ORGANIZATION = "우아한테크코스";
    public static Member POBI = new Member(1L, EMAIL, PASSWORD, ORGANIZATION);

    public static Map LUTHER = new Map(1L, "루터회관", POBI);
    public static Space BE = new Space.Builder()
            .id(1L)
            .name("백엔드 강의실")
            .textPosition("bottom")
            .color("#FED7D9")
            .coordinate("100, 90")
            .map(LUTHER)
            .description(null)
            .area(null)
            .availableStartTime(null)
            .availableEndTime(null)
            .reservationTimeUnit(null)
            .reservationMinimumTimeUnit(null)
            .reservationMaximumTimeUnit(null)
            .reservationEnable(null)
            .disabledWeekdays(null)
            .mapImage(null)
            .build();

    public static Space FE1 = new Space.Builder()
            .id(2L)
            .name("프론트엔드 강의실1")
            .textPosition("bottom")
            .color("#FED7D9")
            .coordinate("560, 40")
            .map(LUTHER)
            .description(null)
            .area(null)
            .availableStartTime(null)
            .availableEndTime(null)
            .reservationTimeUnit(null)
            .reservationMinimumTimeUnit(null)
            .reservationMaximumTimeUnit(null)
            .reservationEnable(null)
            .disabledWeekdays(null)
            .mapImage(null)
            .build();

    public static final LocalDate TOMORROW = LocalDate.now().plusDays(1);
    public static final LocalDateTime TOMORROW_START_TIME = TOMORROW.atStartOfDay();
    public static final String DESCRIPTION = "찜꽁 1차 회의";
    public static final String USER_NAME = "찜꽁";
    public static final String RESERVATION_PASSWORD = "1234";

    public static Reservation BE_AM_ZERO_ONE = new Reservation.Builder()
            .id(1L)
            .startTime(TOMORROW_START_TIME)
            .endTime(TOMORROW_START_TIME.plusHours(1))
            .description(DESCRIPTION)
            .userName(USER_NAME)
            .password(RESERVATION_PASSWORD)
            .space(BE)
            .build();

    public static Reservation BE_PM_ONE_TWO = new Reservation.Builder()
            .id(2L)
            .startTime(TOMORROW.atTime(13, 0, 0))
            .endTime(TOMORROW.atTime(14, 0, 0))
            .description("찜꽁 2차 회의")
            .userName(USER_NAME)
            .password(RESERVATION_PASSWORD)
            .space(BE)
            .build();
}
