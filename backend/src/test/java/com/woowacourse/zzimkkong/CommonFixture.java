package com.woowacourse.zzimkkong;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CommonFixture {
    public static final String EMAIL = "pobi@email.com";
    public static final String PASSWORD = "test1234";
    public static final String ORGANIZATION = "우아한테크코스";
    public static Member POBI = new Member(EMAIL, PASSWORD, ORGANIZATION);

    public static Map LUTHER = new Map("루터회관", POBI);
    public static Space BE = new Space("백엔드 강의실", "#FED7D9", LUTHER);
    public static Space FE1 = new Space("프론트엔드 강의실1", "#FED7D9", LUTHER);

    public static final LocalDate TOMORROW = LocalDate.now().plusDays(1);
    public static final LocalDateTime TOMORROW_START_TIME = TOMORROW.atStartOfDay();
    public static final String DESCRIPTION = "찜꽁 1차 회의";
    public static final String USER_NAME = "찜꽁";
    public static final String RESERVATION_PASSWORD = "1234";

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
