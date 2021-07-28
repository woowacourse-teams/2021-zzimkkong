package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ServiceTestFixture {
    public static final String EMAIL = "pobi@email.com";
    public static final String PASSWORD = "test1234";
    public static final String ORGANIZATION = "우아한테크코스";
    public static Member POBI = new Member(1L, EMAIL, PASSWORD, ORGANIZATION);
    public static Member JASON = new Member(2L, "jason@test.com", PASSWORD, ORGANIZATION);
    public static Map LUTHER = new Map(1L, "루터회관", "mapDrawingData", "mapImageData", POBI);
    public static Setting BE_SETTING = new Setting.Builder()
            .availableStartTime(LocalTime.of(10, 0))
            .availableEndTime(LocalTime.of(22, 0))
            .reservationTimeUnit(30)
            .reservationMinimumTimeUnit(60)
            .reservationMaximumTimeUnit(120)
            .reservationEnable(true)
            .disabledWeekdays("Monday, Tuesday")
            .build();

    public static Space BE = new Space.Builder()
            .id(1L)
            .name("백엔드 강의실")
            .textPosition("bottom")
            .color("#FED7D9")
            .coordinate("100, 90")
            .map(LUTHER)
            .description("우리집")
            .area("프론트 화이팅")
            .setting(BE_SETTING)
            .mapImage("이미지 입니다")
            .build();

    public static Setting FE_SETTING = new Setting.Builder()
            .availableStartTime(LocalTime.of(0, 0))
            .availableEndTime(LocalTime.of(23, 59))
            .reservationTimeUnit(10)
            .reservationMinimumTimeUnit(10)
            .reservationMaximumTimeUnit(1440)
            .reservationEnable(true)
            .disabledWeekdays(null)
            .build();

    public static Space FE1 = new Space.Builder()
            .id(2L)
            .name("프론트엔드 강의실1")
            .textPosition("bottom")
            .color("#FED7D9")
            .coordinate("560, 40")
            .map(LUTHER)
            .description("시니컬하네")
            .area("area")
            .setting(FE_SETTING)
            .mapImage("이미지 입니다")
            .build();

    public static Map SMALL_HOUSE = new Map(2L, "작은집", "mapDrawingData", "mapImageData", POBI);
    public static final LocalDate TOMORROW = LocalDate.now().plusDays(1);
    public static final LocalDateTime TOMORROW_START_TIME = LocalDate.now().plusDays(2L).atTime(10, 0);
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
