package com.woowacourse.zzimkkong.exception.infrastructure;

import org.springframework.http.HttpStatus;

public class CannotDeleteConvertedFileException extends InfrastructureMalfunctionException {
    private static final String MESSAGE = "변환된 이미지를 삭제하는 데에 실패했습니다. 관리자에게 문의하세요.";

    public CannotDeleteConvertedFileException() {
        super(MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
