package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.member.MemberFindResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class MembersResponse {
    private List<MemberFindResponse> members;

    private MembersResponse(List<MemberFindResponse> members) {
        this.members = members;
    }

    public static MembersResponse from(List<Member> members) {
        return members.stream()
                .map(MemberFindResponse::from)
                .collect(Collectors.collectingAndThen(Collectors.toList(), MembersResponse::new));
    }
}
