package com.woowacourse.zzimkkong.dto.reservation;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.*;

@Getter
public class ReservationCreateUpdateWithPasswordRequest extends ReservationCreateUpdateRequest {
    @NotBlank(message = EMPTY_MESSAGE)
    @Pattern(regexp = RESERVATION_PW_FORMAT, message = RESERVATION_PW_MESSAGE)
    private String password;

    public ReservationCreateUpdateWithPasswordRequest() {
    }

    public ReservationCreateUpdateWithPasswordRequest(
            final LocalDateTime startDateTime,
            final LocalDateTime endDateTime,
            final String password,
            final String name,
            final String description) {
        super(startDateTime, endDateTime, name, description);
        this.password = password;
    }
}
