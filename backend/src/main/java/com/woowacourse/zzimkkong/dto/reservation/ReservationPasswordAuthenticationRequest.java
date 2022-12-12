package com.woowacourse.zzimkkong.dto.reservation;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.*;

@Getter
@NoArgsConstructor
public class ReservationPasswordAuthenticationRequest {
    @Pattern(regexp = RESERVATION_PW_FORMAT, message = RESERVATION_PW_MESSAGE)
    private String password;

    public ReservationPasswordAuthenticationRequest(final String password) {
        this.password = password;
    }
}
