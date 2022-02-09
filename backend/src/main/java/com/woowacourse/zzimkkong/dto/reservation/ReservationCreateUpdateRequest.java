package com.woowacourse.zzimkkong.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static com.woowacourse.zzimkkong.dto.ValidatorMessage.*;

@Getter
@NoArgsConstructor
public class ReservationCreateUpdateRequest {
    @JsonFormat(pattern = DATETIME_FORMAT)
    @NotNull(message = EMPTY_MESSAGE)
    protected ZonedDateTime startDateTime;

    @JsonFormat(pattern = DATETIME_FORMAT)
    @NotNull(message = EMPTY_MESSAGE)
    protected ZonedDateTime endDateTime;

    @NotBlank(message = EMPTY_MESSAGE)
    @Pattern(regexp = NAMING_FORMAT, message = NAME_MESSAGE)
    protected String name;

    @NotBlank(message = EMPTY_MESSAGE)
    @Size(max = 100, message = DESCRIPTION_MESSAGE)
    protected String description;
    
    public ReservationCreateUpdateRequest(LocalDateTime startDateTime, LocalDateTime endDateTime, String name, String description) {
        this.startDateTime = TimeZoneUtils.addTimeZone(startDateTime);
        this.endDateTime = TimeZoneUtils.addTimeZone(endDateTime);
        this.name = name;
        this.description = description;
    }
    
    public String getPassword() {
        return null;
    }
    
    public LocalDateTime localStartDateTime() {
        return startDateTime.toLocalDateTime();
    }
    
    public LocalDateTime localEndDateTime() {
        return endDateTime.toLocalDateTime();
    }
}
