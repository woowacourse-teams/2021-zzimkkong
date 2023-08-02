package com.woowacourse.zzimkkong.dto.reservation;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import java.time.ZonedDateTime;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.RESERVATION_PW_FORMAT;
import static com.woowacourse.zzimkkong.dto.ValidatorMessage.RESERVATION_PW_MESSAGE;

@Getter
@NoArgsConstructor
public class ReservationUpdateWithPasswordWithoutEndTimeRequest extends ReservationCreateUpdateRequest {
    @Pattern(regexp = RESERVATION_PW_FORMAT, message = RESERVATION_PW_MESSAGE)
    private String password;

    public ReservationUpdateWithPasswordWithoutEndTimeRequest(
            final ZonedDateTime startDateTime,
            final String password,
            final String name,
            final String description) {
        super(startDateTime, ZonedDateTime.now(), name, description);
        this.password = password;
    }
}
