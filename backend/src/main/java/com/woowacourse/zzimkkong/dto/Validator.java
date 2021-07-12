package com.woowacourse.zzimkkong.dto;

public class Validator {
    public static final String EMPTY_MESSAGE = "비어있는 항목을 입력해주세요.";
    public static final String EMAIL_MESSAGE = "올바른 이메일 형식이 아닙니다.";
    public static final String MEMBER_PASSWORD_MESSAGE = "비밀번호는 영어와 숫자를 포함해서 8자 이상 20자 이내로 입력해주세요.";
    public static final String RESERVATION_PASSWORD_MESSAGE = "비밀번호는 숫자 4자리만 입력 가능합니다.";
    public static final String ORGANIZATION_MESSAGE = "조직명은 특수문자(-_!?.,)를 포함하여 20자 이내로 작성 가능합니다.";
    public static final String NAME_MESSAGE = "이름은 특수문자(-_!?.,)를 포함하여 20자 이내로 작성 가능합니다.";
    public static final String DESCRIPTION_MESSAGE = "예약 내용은 100자 이내로 작성 가능합니다.";

    public static final String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String MEMBER_PASSWORD_FORMAT = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,20}$";
    public static final String RESERVATION_PASSWORD_FORMAT = "^[0-9]{4}$";
    public static final String NAME_FORMAT = "^[-_!?.,a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]{1,20}$";
}
