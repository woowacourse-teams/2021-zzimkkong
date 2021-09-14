package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.dto.member.MemberSaveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.EMPTY_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;

class MemberUpdateRequestTest extends RequestTest {
    @ParameterizedTest
    @NullSource
    @DisplayName("회원 정보 수정 조직명에 빈 문자열이 들어오면 처리한다.")
    void blankOrganization(String organization) {
        MemberSaveRequest memberSaveRequest = new MemberSaveRequest("email@email.com", "password", organization);

        assertThat(getConstraintViolations(memberSaveRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();
    }
}
