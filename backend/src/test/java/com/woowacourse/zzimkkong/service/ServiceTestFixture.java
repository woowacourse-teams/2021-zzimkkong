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

    public static final String MAP_IMAGE_URL = "https://zzimkkong-personal.s3.ap-northeast-2.amazonaws.com/thumbnails/2387563.png";
    public static final String MAP_SVG = "<?xml version='1.0'?><svg fill='#000000' xmlns='http://www.w3.org/2000/svg'  viewBox='0 0 30 30' width='30px' height='30px'>    <path d='M 7 4 C 6.744125 4 6.4879687 4.0974687 6.2929688 4.2929688 L 4.2929688 6.2929688 C 3.9019687 6.6839688 3.9019687 7.3170313 4.2929688 7.7070312 L 11.585938 15 L 4.2929688 22.292969 C 3.9019687 22.683969 3.9019687 23.317031 4.2929688 23.707031 L 6.2929688 25.707031 C 6.6839688 26.098031 7.3170313 26.098031 7.7070312 25.707031 L 15 18.414062 L 22.292969 25.707031 C 22.682969 26.098031 23.317031 26.098031 23.707031 25.707031 L 25.707031 23.707031 C 26.098031 23.316031 26.098031 22.682969 25.707031 22.292969 L 18.414062 15 L 25.707031 7.7070312 C 26.098031 7.3170312 26.098031 6.6829688 25.707031 6.2929688 L 23.707031 4.2929688 C 23.316031 3.9019687 22.682969 3.9019687 22.292969 4.2929688 L 15 11.585938 L 7.7070312 4.2929688 C 7.5115312 4.0974687 7.255875 4 7 4 z'/></svg>";
    public static final String MAP_DRAWING_DATA =
            "{'id': '1', 'type': 'polyline', 'fill': '', 'stroke': 'rgba(111, 111, 111, 1)', 'points': '['60,250', '1,231', '242,252']'," +
                    "'d': '[]', 'transform': ''}";
    public static final String SPACE_DRAWING = "{ 'id':1', 'type' : 'rect', 'x' : 10, 'y' : 10, 'width': 30, 'height': 30 }";

    public static Map LUTHER = new Map(1L, "루터회관", MAP_DRAWING_DATA, MAP_IMAGE_URL, POBI);
    public static Map SMALL_HOUSE = new Map(2L, "작은집", MAP_DRAWING_DATA, MAP_IMAGE_URL, POBI);

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
            .area(SPACE_DRAWING)
            .setting(BE_SETTING)
            .mapImage(MAP_IMAGE_URL)
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
            .area(SPACE_DRAWING)
            .setting(FE_SETTING)
            .mapImage(MAP_IMAGE_URL)
            .build();

    public static final LocalDate THE_DAY_AFTER_TOMORROW = LocalDate.now().plusDays(2L);
    public static final LocalDateTime THE_DAY_AFTER_TOMORROW_START_TIME = LocalDate.now().plusDays(2L).atTime(10, 0);
    public static final String DESCRIPTION = "찜꽁 1차 회의";
    public static final String USER_NAME = "찜꽁";
    public static final String RESERVATION_PASSWORD = "1234";

    public static Reservation BE_AM_ZERO_ONE = new Reservation.Builder()
            .id(1L)
            .startTime(THE_DAY_AFTER_TOMORROW_START_TIME)
            .endTime(THE_DAY_AFTER_TOMORROW_START_TIME.plusHours(1))
            .description(DESCRIPTION)
            .userName(USER_NAME)
            .password(RESERVATION_PASSWORD)
            .space(BE)
            .build();

    public static Reservation BE_PM_ONE_TWO = new Reservation.Builder()
            .id(2L)
            .startTime(THE_DAY_AFTER_TOMORROW.atTime(13, 0, 0))
            .endTime(THE_DAY_AFTER_TOMORROW.atTime(14, 0, 0))
            .description("찜꽁 2차 회의")
            .userName(USER_NAME)
            .password(RESERVATION_PASSWORD)
            .space(BE)
            .build();

    public static Reservation BE_NEXT_DAY_PM_SIX_TWELVE = new Reservation.Builder()
            .id(3L)
            .startTime(THE_DAY_AFTER_TOMORROW.plusDays(1).atTime(18, 0, 0))
            .endTime(THE_DAY_AFTER_TOMORROW.plusDays(1).atTime(23, 59, 59))
            .description("찜꽁 3차 회의")
            .userName(USER_NAME)
            .password("6789")
            .space(BE)
            .build();

    public static Reservation FE1_ZERO_ONE = new Reservation.Builder()
            .id(4L)
            .startTime(THE_DAY_AFTER_TOMORROW.atStartOfDay())
            .endTime(THE_DAY_AFTER_TOMORROW.atTime(1, 0, 0))
            .description("찜꽁 5차 회의")
            .userName(USER_NAME)
            .password(RESERVATION_PASSWORD)
            .space(FE1)
            .build();
}
