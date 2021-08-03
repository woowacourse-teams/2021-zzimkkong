package com.woowacourse.zzimkkong.dto.reservation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.*;

public class ReservationCreateUpdateWithPasswordRequest extends ReservationCreateUpdateRequest {
    @NotBlank(message = EMPTY_MESSAGE)
    @Pattern(regexp = RESERVATION_PASSWORD_FORMAT, message = RESERVATION_PASSWORD_MESSAGE)
    private String password;

    public ReservationCreateUpdateWithPasswordRequest() {
    }

    public ReservationCreateUpdateWithPasswordRequest(
            final Long spaceId,
            final LocalDateTime startDateTime,
            final LocalDateTime endDateTime,
            final String password,
            final String name,
            final String description) {
        super(spaceId, startDateTime, endDateTime, name, description);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
