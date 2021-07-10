package com.woowacourse.zzimkkong.dto;

import com.woowacourse.zzimkkong.exception.ImpossibleEndTimeException;
import com.woowacourse.zzimkkong.exception.ImpossibleStartTimeException;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class ReservationSaveRequest {
    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private Long spaceId;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private LocalDateTime startDateTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @NotNull(message = "비어있는 항목을 입력해주세요.")
    private LocalDateTime endDateTime;

    @NotBlank(message = "비어있는 항목을 입력해주세요.")
    @Pattern(regexp = "^[0-9]{4}$", message = "비밀번호는 숫자 4자리만 입력 가능합니다.")
    private String password;

    @NotBlank(message = "비어있는 항목을 입력해주세요.")
    @Pattern(regexp = "^[-_!?.,a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]{1,20}$", message = "이름은 특수문자(-_!?.,)를 포함하여 20자 이내로 작성 가능합니다.")
    private String name;

    @NotBlank(message = "비어있는 항목을 입력해주세요.")
    @Size(max = 100, message = "예약 내용은 100자 이내로 작성 가능합니다.")
    private String description;

    public ReservationSaveRequest() {
    }

    public ReservationSaveRequest(
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

    public void checkValidateTime() {
        if (startDateTime.isBefore(LocalDateTime.now())) {
            throw new ImpossibleStartTimeException();
        }

        if (endDateTime.isBefore(startDateTime)) {
            throw new ImpossibleEndTimeException();
        }
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
