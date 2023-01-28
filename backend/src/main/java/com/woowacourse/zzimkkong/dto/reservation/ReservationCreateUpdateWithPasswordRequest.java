package com.woowacourse.zzimkkong.dto.reservation;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import java.time.ZonedDateTime;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.RESERVATION_PW_FORMAT;
import static com.woowacourse.zzimkkong.dto.ValidatorMessage.RESERVATION_PW_MESSAGE;

@Getter
@NoArgsConstructor
public class ReservationCreateUpdateWithPasswordRequest extends ReservationCreateUpdateRequest {
    @Pattern(regexp = RESERVATION_PW_FORMAT, message = RESERVATION_PW_MESSAGE)
    private String password;

    public ReservationCreateUpdateWithPasswordRequest(
            final ZonedDateTime startDateTime,
            final ZonedDateTime endDateTime,
            final String password,
            final String name,
            final String description) {
        super(startDateTime, endDateTime, name, description);
        this.password = password;
    }
}
