package com.woowacourse.zzimkkong.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class MemberSaveRequestDto {
    @NotBlank(message = "이메일은 공백일 수 없습니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @NotBlank(message = "비밀번호는 공백일 수 없습니다")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z]).{8,}", message = "비밀번호는 영어와 숫자를 포함해서 8자 이상 입력해주세요")
    private String password;

    @NotBlank(message = "조직명은 공백일 수 없습니다")
    //todo 조직명 특수문자 일부허용 검증로직 추가
    private String organization;

    public MemberSaveRequestDto() {
    }

    public MemberSaveRequestDto(final String email, final String password, final String organization) {
        this.email = email;
        this.password = password;
        this.organization = organization;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getOrganization() {
        return organization;
    }
}
