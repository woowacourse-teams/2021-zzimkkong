package com.woowacourse.zzimkkong.exception.infrastructure;

import org.springframework.http.HttpStatus;

public class SvgToPngConvertException extends InfrastructureException {
    private static final String MESSAGE = "svg 데이터를 png 파일로 변환할 수 없습니다. 데이터 형식을 확인해주세요.";

    public SvgToPngConvertException(Exception exception) {
        super(MESSAGE, exception, HttpStatus.BAD_REQUEST, MAP_IMAGE_SVG);
    }
}
