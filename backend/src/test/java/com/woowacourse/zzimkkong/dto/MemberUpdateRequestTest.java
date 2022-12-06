package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.dto.member.MemberSaveRequest;
import com.woowacourse.zzimkkong.dto.member.MemberUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.EMPTY_MESSAGE;
import static com.woowacourse.zzimkkong.dto.ValidatorMessage.NAME_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;

class MemberUpdateRequestTest extends RequestTest {
    @ParameterizedTest
    @NullSource
    @DisplayName("회원 정보 수정 조직명에 빈 문자열이 들어오면 처리한다.")
    void blankOrganization(String organization) {
        MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest(organization, "sakjung");

        assertThat(getConstraintViolations(memberUpdateRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();

    }

    @ParameterizedTest
    @NullSource
    @DisplayName("회원 정보 수정 유저 이름에 빈 문자열이 들어오면 처리한다.")
    void blankUserName(String userName) {
        MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest("조직이다", userName);

        assertThat(getConstraintViolations(memberUpdateRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(EMPTY_MESSAGE)))
                .isTrue();

    }

    @ParameterizedTest
    @CsvSource(value = {"hihellomorethantwenty:true", "허용되지않은놈$#@:true", "안 녕 하 세 요:true", "한글조직:false", "hihello:false", "ㄱㄴ힣ㄷㄹ:false"}, delimiter = ':')
    @DisplayName("회원 정보 수정 유저 이름에 옳지 않은 형식의 문자열이 들어오면 처리한다.")
    void invalidUserName(String userName, boolean flag) {
        MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest("organization",  userName);

        assertThat(getConstraintViolations(memberUpdateRequest).stream()
                .anyMatch(violation -> violation.getMessage().equals(NAME_MESSAGE)))
                .isEqualTo(flag);
    }
}
