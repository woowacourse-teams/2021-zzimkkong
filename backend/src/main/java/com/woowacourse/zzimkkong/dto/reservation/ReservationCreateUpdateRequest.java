package com.woowacourse.zzimkkong.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.*;
import static com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils.UTC;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservationCreateUpdateRequest {
    @JsonFormat(pattern = DATETIME_FORMAT)
    @NotNull(message = EMPTY_MESSAGE)
    protected ZonedDateTime startDateTime;

    @JsonFormat(pattern = DATETIME_FORMAT)
    @NotNull(message = EMPTY_MESSAGE)
    protected ZonedDateTime endDateTime;

    @Pattern(regexp = NAMING_FORMAT, message = NAME_MESSAGE)
    protected String name;

    @NotBlank(message = EMPTY_MESSAGE)
    @Size(max = 100, message = DESCRIPTION_MESSAGE)
    protected String description;

    public ReservationCreateUpdateRequest(ZonedDateTime startDateTime, ZonedDateTime endDateTime, String name, String description) {
        if (startDateTime != null) {
            this.startDateTime = startDateTime.withZoneSameInstant(UTC.toZoneId());
        }
        if (endDateTime != null) {
            this.endDateTime = endDateTime.withZoneSameInstant(UTC.toZoneId());
        }
        this.name = name;
        this.description = description;
    }

    public String getPassword() {
        return null;
    }

    public String getEmail() {
        return null;
    }

    public LocalDateTime localStartDateTime() {
        return startDateTime.toLocalDateTime();
    }

    public LocalDateTime localEndDateTime() {
        return endDateTime.toLocalDateTime();
    }
}
