package com.woowacourse.zzimkkong.dto;

public class ValidatorMessage {
    public static final String EMPTY_MESSAGE = "비어있는 항목을 입력해주세요.";
    public static final String EMAIL_MESSAGE = "올바른 이메일 형식이 아닙니다.";
    public static final String MEMBER_PASSWORD_MESSAGE = "비밀번호는 영어와 숫자를 포함해서 8자 이상 20자 이내로 입력해주세요.";
    public static final String RESERVATION_PASSWORD_MESSAGE = "비밀번호는 숫자 4자리만 입력 가능합니다.";
    public static final String ORGANIZATION_MESSAGE = "조직명은 특수문자 없이 20자 이내로 작성 가능합니다.";
    public static final String NAME_MESSAGE = "이름은 특수문자(-_!?.,)를 포함하여 20자 이내로 작성 가능합니다.";
    public static final String DESCRIPTION_MESSAGE = "예약 내용은 100자 이내로 작성 가능합니다.";
    public static final String FORMAT_MESSAGE = "날짜 및 시간 데이터 형식이 올바르지 않습니다.";
    public static final String SERVER_ERROR_MESSAGE = "예상치 못한 문제가 발생했습니다. 개발자에게 문의하세요.";

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String MEMBER_PASSWORD_FORMAT = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9]{8,20}$";
    public static final String RESERVATION_PASSWORD_FORMAT = "^[0-9]{4}$";
    public static final String ORGANIZATION_FORMAT = "^[ a-zA-Z0-9ㄱ-힣]{1,20}$";
    public static final String NAMING_FORMAT = "^[-_!?.,a-zA-Z0-9ㄱ-힣]{1,20}$";
}
