package com.woowacourse.zzimkkong.exception.infrastructure;

import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

public class NoMasterDataSourceException extends ZzimkkongException {
    private static final String MESSAGE = "Master DB의 DataSource 설정이 올바르지 않습니다.";

    public NoMasterDataSourceException() {
        super(MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
