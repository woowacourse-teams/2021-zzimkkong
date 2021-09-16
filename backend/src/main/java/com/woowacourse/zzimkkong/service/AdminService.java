package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.MembersResponse;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminService {
    private final MemberRepository members;

    public AdminService(MemberRepository members) {
        this.members = members;
    }

    public MembersResponse findMembers() {
        List<Member> allMembers = members.findAll();
        return MembersResponse.from(allMembers);
    }
}
