package com.woowacourse.zzimkkong.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class ReservationPasswordAuthenticationRequest {
    @NotBlank(message = "비어있는 항목을 입력해주세요.")
    @Pattern(regexp = "^[0-9]{4}$", message = "비밀번호는 숫자 4자리만 입력 가능합니다.")
    private String password;

    public ReservationPasswordAuthenticationRequest() {
    }

    public ReservationPasswordAuthenticationRequest(final String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
