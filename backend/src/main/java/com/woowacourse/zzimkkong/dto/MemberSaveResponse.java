package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.domain.Member;

public class MemberSaveResponse {
    private Long id;

    public MemberSaveResponse() {

    }

    private MemberSaveResponse(final Long id) {
        this.id = id;
    }

    public static MemberSaveResponse of(Member member) {
        return new MemberSaveResponse(member.getId());
    }

    public Long getId() {
        return id;
    }
}
