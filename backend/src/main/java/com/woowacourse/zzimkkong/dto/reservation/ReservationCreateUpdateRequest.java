package com.woowacourse.zzimkkong.dto.reservation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.*;

@Getter
@NoArgsConstructor
public class ReservationCreateUpdateRequest {
    @DateTimeFormat(pattern = DATETIME_FORMAT)
    @NotNull(message = EMPTY_MESSAGE)
    protected LocalDateTime startDateTime;

    @DateTimeFormat(pattern = DATETIME_FORMAT)
    @NotNull(message = EMPTY_MESSAGE)
    protected LocalDateTime endDateTime;

    @NotBlank(message = EMPTY_MESSAGE)
    @Pattern(regexp = NAMING_FORMAT, message = NAME_MESSAGE)
    protected String name;

    @NotBlank(message = EMPTY_MESSAGE)
    @Size(max = 100, message = DESCRIPTION_MESSAGE)
    protected String description;

    public ReservationCreateUpdateRequest(
            final LocalDateTime startDateTime,
            final LocalDateTime endDateTime,
            final String name,
            final String description) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.name = name;
        this.description = description;
    }

    public String getPassword() {
        return null;
    }
}
