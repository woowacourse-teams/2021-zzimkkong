package com.woowacourse.zzimkkong.dto.reservation;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.*;

@Getter
public class ReservationPasswordAuthenticationRequest {
    @NotBlank(message = EMPTY_MESSAGE)
    @Pattern(regexp = RESERVATION_PW_FORMAT, message = RESERVATION_PW_MESSAGE)
    private String password;

    public ReservationPasswordAuthenticationRequest() {
    }

    public ReservationPasswordAuthenticationRequest(final String password) {
        this.password = password;
    }
}
