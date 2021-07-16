package com.woowacourse.zzimkkong.dto.reservation;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static com.woowacourse.zzimkkong.dto.Validator.*;

public class ReservationCreateUpdateRequest {
    @NotNull(message = EMPTY_MESSAGE)
    private Long spaceId;

    @DateTimeFormat(pattern = DATETIME_FORMAT)
    @NotNull(message = EMPTY_MESSAGE)
    private LocalDateTime startDateTime;

    @DateTimeFormat(pattern = DATETIME_FORMAT)
    @NotNull(message = EMPTY_MESSAGE)
    private LocalDateTime endDateTime;

    @NotBlank(message = EMPTY_MESSAGE)
    @Pattern(regexp = RESERVATION_PASSWORD_FORMAT, message = RESERVATION_PASSWORD_MESSAGE)
    private String password;

    @NotBlank(message = EMPTY_MESSAGE)
    @Pattern(regexp = NAMING_FORMAT, message = NAME_MESSAGE)
    private String name;

    @NotBlank(message = EMPTY_MESSAGE)
    @Size(max = 100, message = DESCRIPTION_MESSAGE)
    private String description;

    public ReservationCreateUpdateRequest() {
    }

    public ReservationCreateUpdateRequest(
            final Long spaceId,
            final LocalDateTime startDateTime,
            final LocalDateTime endDateTime,
            final String password,
            final String name,
            final String description) {
        this.spaceId = spaceId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
