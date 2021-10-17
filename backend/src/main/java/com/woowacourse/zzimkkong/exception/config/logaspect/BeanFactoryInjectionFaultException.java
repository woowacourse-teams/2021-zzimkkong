package com.woowacourse.zzimkkong.exception.config.logaspect;

public class BeanFactoryInjectionFaultException extends IllegalArgumentException {
    private static final String MESSAGE = "LogAspect에 필요한 BeanFactory가 주입되지 않았습니다. 설정을 확인해주세요.";


}
