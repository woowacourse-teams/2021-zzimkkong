package com.woowacourse.zzimkkong.dto;

public class MemberSaveResponseDto {
    private final Long id;

    public MemberSaveResponseDto(final Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
