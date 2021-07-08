package com.woowacourse.zzimkkong.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class MemberSaveRequestDto {
    @NotBlank(message = "비어있는 항목을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotNull(message = "비어있는 항목을 입력해주세요.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,20}$", message = "비밀번호는 영어와 숫자를 포함해서 8자 이상 20자 이내로 입력해주세요.")
    private String password;

    @NotNull(message = "비어있는 항목을 입력해주세요.")
    @Pattern(regexp = "^[-_!?.,a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]{1,20}$", message = "조직명은 특수문자(-_!?.,)를 포함하여 20자 이내로 작성 가능합니다.")
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
