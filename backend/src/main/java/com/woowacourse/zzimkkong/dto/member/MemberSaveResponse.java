package com.woowacourse.zzimkkong.dto.member;

import com.woowacourse.zzimkkong.domain.Member;

public class MemberSaveResponse {
    private Long id;

    public MemberSaveResponse() {

    }

    private MemberSaveResponse(final Long id) {
        this.id = id;
    }

    public static MemberSaveResponse from(Member member) {
        return new MemberSaveResponse(member.getId());
    }

    public Long getId() {
        return id;
    }
}
