package com.woowacourse.zzimkkong.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.woowacourse.zzimkkong.dto.Validator.*;

public class ReservationDeleteRequest {
    @NotBlank(message = EMPTY_MESSAGE)
    @Pattern(regexp = RESERVATION_PASSWORD_FORMAT, message = MEMBER_PASSWORD_MESSAGE)
    private String password;

    public ReservationDeleteRequest() {
    }

    public ReservationDeleteRequest(final String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
