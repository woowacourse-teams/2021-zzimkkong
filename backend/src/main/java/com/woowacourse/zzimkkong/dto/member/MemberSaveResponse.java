package com.woowacourse.zzimkkong.dto.member;

public class MemberSaveResponse {
    private Long id;

    public MemberSaveResponse() {

    }

    public MemberSaveResponse(final Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
