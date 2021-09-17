package com.woowacourse.zzimkkong.dto.member;

import com.woowacourse.zzimkkong.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberFindResponse {
    private Long id;
    private String email;
    private String organization;

    private MemberFindResponse(final Long id, final String email, final String organization) {
        this.id = id;
        this.email = email;
        this.organization = organization;
    }

    public static MemberFindResponse from(final Member member) {
        return new MemberFindResponse(
                member.getId(),
                member.getEmail(),
                member.getOrganization());
    }
}
