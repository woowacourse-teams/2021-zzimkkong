package com.woowacourse.zzimkkong.dto.member;

import com.woowacourse.zzimkkong.domain.Member;
import lombok.Getter;

@Getter
public class MemberSaveResponse {
    private Long id;

    public MemberSaveResponse() {
    }

    private MemberSaveResponse(final Long id) {
        this.id = id;
    }

    public static MemberSaveResponse from(final Member member) {
        return new MemberSaveResponse(member.getId());
    }
}
