package com.woowacourse.zzimkkong;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Space;

public class CommonFixture {
    public static final String EMAIL = "pobi@email.com";
    public static final String PASSWORD = "test1234";
    public static final String ORGANIZATION = "우아한테크코스";
    public static Member POBI = new Member(EMAIL, PASSWORD, ORGANIZATION);

    public static Map LUTHER = new Map("루터회관", POBI);
    public static Space BE = new Space("백엔드 강의실", LUTHER);
    public static Space FE1 = new Space("프론트엔드 강의실1", LUTHER);
}
