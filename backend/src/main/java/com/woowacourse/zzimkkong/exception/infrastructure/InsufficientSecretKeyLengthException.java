package com.woowacourse.zzimkkong.exception.infrastructure;

import org.springframework.http.HttpStatus;

public class InsufficientSecretKeyLengthException extends SharingIdException {
    private static final String MESSAGE = "공유링크 생성시 사용되는 암호화 키값의 길이(32자 이상)가 충분하지 못합니다. " +
            "관리자는 설정 파일(config/aes256Transcoder.properties)의 키값을 재설정한 후 다시 서버를 구동해주시기 바랍니다.";

    public InsufficientSecretKeyLengthException() {
        super(MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
