package com.woowacourse.zzimkkong.exception.infrastructure;

import org.springframework.http.HttpStatus;

public class PngDeleteException extends InfrastructureException {
    private static final String MESSAGE = "png파일 삭제에 실패했습니다.";

    public PngDeleteException() {
        super(MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, MAP_IMAGE_SVG);
    }
}
