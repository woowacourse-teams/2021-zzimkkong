package com.woowacourse.zzimkkong.service;

import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.dto.admin.MembersResponse;
import com.woowacourse.zzimkkong.dto.admin.PageInfo;
import com.woowacourse.zzimkkong.dto.member.MemberFindResponse;
import com.woowacourse.zzimkkong.exception.member.PasswordMismatchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class AdminServiceTest extends ServiceTest {
    private Member pobi;

    @Autowired
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        pobi = new Member(EMAIL, passwordEncoder.encode(PW), ORGANIZATION);
    }

    @Test
    @DisplayName("어드민 관리자 로그인 아이디, 비밀번호가 옳지 않으면 에러가 발생한다.")
    void loginException() {
        assertThatThrownBy(() -> adminService.login("zzimkkong", "wrong"))
                .isInstanceOf(PasswordMismatchException.class);
    }

    @Test
    @DisplayName("모든 멤버를 페이지네이션을 이용해 조회한다.")
    void findMembers() {
        //given
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.unsorted());
        given(members.findAll(any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(pobi), pageRequest, 1));

        //when
        MembersResponse membersResponse = MembersResponse.from(
                List.of(MemberFindResponse.from(pobi)),
                PageInfo.from(0, 1, 20, 1)
        );
        MembersResponse members = adminService.findMembers(pageRequest);

        //then
        assertThat(members).usingRecursiveComparison()
                .isEqualTo(membersResponse);
    }
}
