package com.woowacourse.zzimkkong.exception.config.logaspect;

public class BeanToReplaceDoesNotExistsException extends IllegalArgumentException {
    private static final String MESSAGE = "로그 프록시로 등록하려는 클래스(%s)와 일치하는 빈이 Application Context에 존재하지 않습니다.";

    public BeanToReplaceDoesNotExistsException(Class<?> clazz) {
        super(String.format(MESSAGE, clazz.getName()));
    }
}
