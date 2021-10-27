package com.woowacourse.zzimkkong.exception.config.logaspect;

public class InvalidModifiableBeanFactoryException extends IllegalArgumentException {
    private static final String MESSAGE = "프로그램에 사용되는 Bean Factory의 구현체로부터는 로그 프록시 빈 대체 과정을 수행할 수 없습니다. " +
            "Bean Factory는 반드시 ConfigurableListableBeanFactory와 BeanDefinitionRegistry에 대한 구현체여야 합니다. " +
            "Application Context 생성 관련 설정을 확인해주세요.";

    public InvalidModifiableBeanFactoryException() {
        super(MESSAGE);
    }
}
