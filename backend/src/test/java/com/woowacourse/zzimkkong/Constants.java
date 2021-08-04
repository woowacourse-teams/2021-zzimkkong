package com.woowacourse.zzimkkong;

import java.time.LocalDate;
import java.time.LocalTime;

public class Constants {
    public static final String EMAIL = "pobi@email.com";
    public static final String NEW_EMAIL = "sakjung@email.com";
    public static final String PASSWORD = "test1234";
    public static final String ORGANIZATION = "우아한테크코스";
    public static final LocalDate THE_DAY_AFTER_TOMORROW = LocalDate.now().plusDays(2);

    public static final String DESCRIPTION = "찜꽁 1차 회의";
    public static final String USER_NAME = "찜꽁";
    public static final String RESERVATION_PASSWORD = "1234";
    public static final String MAP_IMAGE_URL = "https://zzimkkong-personal.s3.ap-northeast-2.amazonaws.com/thumbnails/2387563.png";
    public static final String MAP_SVG = "<?xml version='1.0'?><svg fill='#000000' xmlns='http://www.w3.org/2000/svg'  viewBox='0 0 30 30' width='30px' height='30px'>    <path d='M 7 4 C 6.744125 4 6.4879687 4.0974687 6.2929688 4.2929688 L 4.2929688 6.2929688 C 3.9019687 6.6839688 3.9019687 7.3170313 4.2929688 7.7070312 L 11.585938 15 L 4.2929688 22.292969 C 3.9019687 22.683969 3.9019687 23.317031 4.2929688 23.707031 L 6.2929688 25.707031 C 6.6839688 26.098031 7.3170313 26.098031 7.7070312 25.707031 L 15 18.414062 L 22.292969 25.707031 C 22.682969 26.098031 23.317031 26.098031 23.707031 25.707031 L 25.707031 23.707031 C 26.098031 23.316031 26.098031 22.682969 25.707031 22.292969 L 18.414062 15 L 25.707031 7.7070312 C 26.098031 7.3170312 26.098031 6.6829688 25.707031 6.2929688 L 23.707031 4.2929688 C 23.316031 3.9019687 22.682969 3.9019687 22.292969 4.2929688 L 15 11.585938 L 7.7070312 4.2929688 C 7.5115312 4.0974687 7.255875 4 7 4 z'/></svg>";
    public static final String SPACE_DRAWING = "{ 'id': '1', 'type' : 'rect', 'x': '10', 'y': '10', 'width': '30', 'height': '30' }";
    public static final String LUTHER_NAME = "루터회관";
    public static final String SMALL_HOUSE_NAME = "작은집";
    public static final String MAP_DRAWING_DATA =
            "{'id': '1', 'type': 'polyline', 'fill': '', 'stroke': 'rgba(111, 111, 111, 1)', 'points': '['60,250', '1,231', '242,252']', 'd': '[]', 'transform': ''}";

    public static final LocalTime BE_AVAILABLE_START_TIME = LocalTime.of(0, 0);
    public static final LocalTime BE_AVAILABLE_END_TIME = LocalTime.of(18, 0);
    public static final Integer BE_RESERVATION_TIME_UNIT = 10;
    public static final Integer BE_RESERVATION_MINIMUM_TIME_UNIT = 10;
    public static final Integer BE_RESERVATION_MAXIMUM_TIME_UNIT = 1440;
    public static final Boolean BE_RESERVATION_ENABLE = true;
    public static final String BE_ENABLED_DAY_OF_WEEK = "monday,tuesday,wednesday,thursday,friday,saturday,sunday";

    public static final LocalTime FE_AVAILABLE_START_TIME = LocalTime.of(0, 0);
    public static final LocalTime FE_AVAILABLE_END_TIME = LocalTime.of(18, 0);
    public static final Integer FE_RESERVATION_TIME_UNIT = 10;
    public static final Integer FE_RESERVATION_MINIMUM_TIME_UNIT = 10;
    public static final Integer FE_RESERVATION_MAXIMUM_TIME_UNIT = 1440;
    public static final Boolean FE_RESERVATION_ENABLE = true;
    public static final String FE_ENABLED_DAY_OF_WEEK = "monday,tuesday,wednesday,thursday,friday,saturday,sunday";

    public static final String BE_NAME = "백엔드 강의실";
    public static final String BE_COLOR = "#FED7D9";
    public static final String BE_DESCRIPTION = "시니컬하네";
    public static final String FE_NAME = "프론트엔드 강의실1";
    public static final String FE_COLOR = "#FFCCCC";
    public static final String FE_DESCRIPTION = "시니컬하네";
}
