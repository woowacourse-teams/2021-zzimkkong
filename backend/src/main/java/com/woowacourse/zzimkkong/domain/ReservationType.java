package com.woowacourse.zzimkkong.domain;

import com.woowacourse.zzimkkong.dto.member.LoginUserEmail;
import com.woowacourse.zzimkkong.exception.ZzimkkongException;

import java.util.Arrays;
import java.util.function.BiPredicate;

public enum ReservationType {
    NON_LOGIN_GUEST((apiType, reservationUserEmail) -> Constants.GUEST.equals(apiType) && !reservationUserEmail.exists()),
    LOGIN_GUEST((apiType, reservationUserEmail) -> Constants.GUEST.equals(apiType) && reservationUserEmail.exists()),
    NON_LOGIN_MANAGER((apiType, reservationUserEmail) -> Constants.MANAGER.equals(apiType) && !reservationUserEmail.exists()),
    LOGIN_MANAGER((apiType, reservationUserEmail) -> Constants.MANAGER.equals(apiType) && reservationUserEmail.exists());

    private final BiPredicate<String, LoginUserEmail> expression;

    ReservationType(final BiPredicate<String, LoginUserEmail> expression) {
        this.expression = expression;
    }

    public static ReservationType of(final String apiType, final LoginUserEmail reservationUserEmail) {
        return Arrays.stream(values())
                .filter(value -> value.expression.test(apiType, reservationUserEmail))
                .findFirst()
                .orElseThrow(ZzimkkongException::new);
    }

    public static class Constants {
        public static final String GUEST = "GUEST";
        public static final String MANAGER = "MANAGER";
    }
}
