package com.woowacourse.zzimkkong.dto.reservation;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.*;

public class ReservationCreateUpdateRequest {
    @NotNull(message = EMPTY_MESSAGE)
    protected Long spaceId;

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

    public ReservationCreateUpdateRequest() {
    }

    public ReservationCreateUpdateRequest(
            final Long spaceId,
            final LocalDateTime startDateTime,
            final LocalDateTime endDateTime,
            final String name,
            final String description) {
        this.spaceId = spaceId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.name = name;
        this.description = description;
    }

    public Long getSpaceId() {
        return spaceId;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
