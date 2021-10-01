package com.woowacourse.zzimkkong.dto.admin;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.member.MemberFindResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
public class MembersResponse {
    private List<MemberFindResponse> members;
    private PageInfo pageInfo;

    private MembersResponse(List<MemberFindResponse> members, PageInfo pageInfo) {
        this.members = members;
        this.pageInfo = pageInfo;
    }

    public static MembersResponse of(List<MemberFindResponse> members, PageInfo pageInfo) {
        return new MembersResponse(members, pageInfo);
    }

    public static MembersResponse from(Page<Member> memberPage) {
        List<MemberFindResponse> responses = memberPage.map(MemberFindResponse::from).getContent();
        return new MembersResponse(responses, PageInfo.from(memberPage));
    }
}
