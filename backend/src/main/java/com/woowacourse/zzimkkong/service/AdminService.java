package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.dto.admin.MembersResponse;
import com.woowacourse.zzimkkong.dto.admin.PageInfo;
import com.woowacourse.zzimkkong.dto.member.MemberFindResponse;
import com.woowacourse.zzimkkong.exception.member.PasswordMismatchException;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdminService {
    private static final String ADMIN_ID = "zzimkkong";
    private static final String ADMIN_PWD = "zzimkkong1!";

    private final MemberRepository members;

    public AdminService(MemberRepository members) {
        this.members = members;
    }

    public void login(final String id, final String password) {
        if (!id.equals(ADMIN_ID) || !password.equals(ADMIN_PWD)) {
            throw new PasswordMismatchException();
        }
    }

    public MembersResponse findMembers(Pageable pageable) {
        Page<MemberFindResponse> allMembers = members.findAll(pageable)
                .map(MemberFindResponse::from);

        return assemble(allMembers);
    }

    private MembersResponse assemble(Page<MemberFindResponse> data) {
        PageInfo pageInfo = PageInfo.from(
                data.getPageable().getPageNumber(),
                data.getTotalPages(),
                data.getPageable().getPageSize(),
                data.getTotalElements());
        return MembersResponse.from(data.getContent(), pageInfo);
    }
}
