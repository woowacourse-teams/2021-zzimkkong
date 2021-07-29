package com.woowacourse.zzimkkong;

import com.woowacourse.zzimkkong.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CommonFixture {
    public static final String EMAIL = "pobi@email.com";
    public static final String NEW_EMAIL = "sakjung@email.com";
    public static final String PASSWORD = "test1234";
    public static final String ORGANIZATION = "우아한테크코스";
    public static final LocalDate TOMORROW = LocalDate.now().plusDays(1);
    public static final LocalDateTime TOMORROW_START_TIME = TOMORROW.atStartOfDay();
    public static final String DESCRIPTION = "찜꽁 1차 회의";
    public static final String USER_NAME = "찜꽁";
    public static final String RESERVATION_PASSWORD = "1234";
    public static Member POBI = new Member(EMAIL, PASSWORD, ORGANIZATION);
    public static Member JASON = new Member("jason@test.com", PASSWORD, ORGANIZATION);
    public static Map LUTHER = new Map("루터회관", "mapDrawingData", "mapImageData", POBI);
    public static Map SMALL_HOUSE = new Map("작은집", "mapDrawingData", "mapImageData", POBI);
    public static Setting BE_SETTING = new Setting.Builder()
            .availableStartTime(LocalTime.of(0, 0))
            .availableEndTime(LocalTime.of(23, 59))
            .reservationTimeUnit(10)
            .reservationMinimumTimeUnit(10)
            .reservationMaximumTimeUnit(1440)
            .reservationEnable(true)
            .disabledWeekdays(null)
            .build();
    public static Space BE = new Space.Builder()
            .name("백엔드 강의실")
            .textPosition("bottom")
            .color("#FED7D9")
            .coordinate("100, 90")
            .map(LUTHER)
            .description("시니컬하네")
            .area("area")
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
    public static Reservation BE_AM_ZERO_ONE = new Reservation.Builder()
            .startTime(TOMORROW_START_TIME)
            .endTime(TOMORROW_START_TIME.plusHours(1))
            .description(DESCRIPTION)
            .userName(USER_NAME)
            .password(RESERVATION_PASSWORD)
            .space(BE)
            .build();

    public static Reservation BE_PM_ONE_TWO = new Reservation.Builder()
            .startTime(TOMORROW.atTime(13, 0, 0))
            .endTime(TOMORROW.atTime(14, 0, 0))
            .description("찜꽁 2차 회의")
            .userName(USER_NAME)
            .password(RESERVATION_PASSWORD)
            .space(BE)
            .build();

    public static Reservation BE_NEXT_DAY_PM_SIX_TWELVE = new Reservation.Builder()
            .startTime(TOMORROW.plusDays(1).atTime(18, 0, 0))
            .endTime(TOMORROW.plusDays(1).atTime(23, 59, 59))
            .description("찜꽁 3차 회의")
            .userName(USER_NAME)
            .password("6789")
            .space(BE)
            .build();

    public static Reservation FE1_ZERO_ONE = new Reservation.Builder()
            .startTime(TOMORROW.atStartOfDay())
            .endTime(TOMORROW.atTime(1, 0, 0))
            .description("찜꽁 5차 회의")
            .userName(USER_NAME)
            .password(RESERVATION_PASSWORD)
            .space(FE1)
            .build();
}
