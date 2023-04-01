package com.woowacourse.zzimkkong.dto;

public class ValidatorMessage {
    private ValidatorMessage() {
    }

    public static final String EMPTY_MESSAGE = "비어있는 항목을 입력해주세요.";
    public static final String EMAIL_MESSAGE = "올바른 이메일 형식이 아닙니다.";
    public static final String MEMBER_PW_MESSAGE = "비밀번호는 영어와 숫자를 포함해서 8자 이상 20자 이내로 입력해주세요.";
    public static final String RESERVATION_PW_MESSAGE = "비밀번호는 숫자 4자리만 입력 가능합니다.";
    public static final String ORGANIZATION_MESSAGE = "조직명은 특수문자 없이 20자 이내로 작성 가능합니다.";
    public static final String NAME_MESSAGE = "이름은 특수문자(-_!?.,)를 포함하여 공백없이 20자 이내로 작성 가능합니다.";
    public static final String PRESET_NAME_MESSAGE = "프리셋 이름은 공백과 특수문자(-_!?.,)를 포함하여 20자 이내로 작성 가능합니다.";
    public static final String DESCRIPTION_MESSAGE = "예약 내용은 100자 이내로 작성 가능합니다.";
    public static final String NOTICE_MESSAGE = "공지사항은 100자 이내로 작성 가능합니다.";
    public static final String FORMAT_MESSAGE = "날짜 및 시간 데이터 형식이 올바르지 않습니다.";
    public static final String DAY_OF_WEEK_MESSAGE = "올바른 요일 형식이 아닙니다.";
    public static final String TIME_UNIT_MESSAGE = "시간 단위는 10, 30, 60, 120입니다.";
    public static final String SETTING_COUNT_MESSAGE = "공간의 예약 조건이 최소 1개는 존재해야 합니다.";
    public static final String NEGATIVE_SETTING_ORDER_MESSAGE = "공간 예약 조건 우선순위 값은 음수일 수 없습니다.";
    public static final String DUPLICATE_SETTING_ORDER_MESSAGE = "공간 예약 조건 우선순위 값은 서로 중복될 수 없습니다.";
    public static final String SERVER_ERROR_MESSAGE = "일시적으로 접속이 원활하지 않습니다. 잠시 후 다시 이용해 주시기 바랍니다.";

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssxxx";
    public static final String MEMBER_PW_FORMAT = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,20}$";
    public static final String RESERVATION_PW_FORMAT = "^[0-9]{4}$";
    public static final String ORGANIZATION_FORMAT = "^[ a-zA-Z0-9ㄱ-힣]{1,20}$";
    public static final String NAMING_FORMAT = "^[-_!?.,a-zA-Z0-9ㄱ-힣]{1,20}$";
    public static final String PRESET_NAME_FORMAT = "^[-_!?., a-zA-Z0-9ㄱ-힣]{1,20}$";
}
