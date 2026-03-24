package com.woowacourse.zzimkkong.dto.reservation;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.DESCRIPTION_MESSAGE;
import static com.woowacourse.zzimkkong.dto.ValidatorMessage.EMPTY_MESSAGE;
import static com.woowacourse.zzimkkong.dto.ValidatorMessage.NAME_MESSAGE;
import static com.woowacourse.zzimkkong.dto.ValidatorMessage.NAMING_FORMAT;
import static com.woowacourse.zzimkkong.dto.ValidatorMessage.RESERVATION_PW_FORMAT;
import static com.woowacourse.zzimkkong.dto.ValidatorMessage.RESERVATION_PW_MESSAGE;

@Getter
@NoArgsConstructor
public class ReservationManagerEarlyStopRequest {
    @Pattern(regexp = RESERVATION_PW_FORMAT, message = RESERVATION_PW_MESSAGE)
    private String password;

    @Pattern(regexp = NAMING_FORMAT, message = NAME_MESSAGE)
    protected String name;

    @NotBlank(message = EMPTY_MESSAGE)
    @Size(max = 100, message = DESCRIPTION_MESSAGE)
    protected String description;
}
