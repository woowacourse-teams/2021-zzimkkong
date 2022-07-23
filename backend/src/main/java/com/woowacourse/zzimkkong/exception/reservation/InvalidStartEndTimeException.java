package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.domain.Settings;
import com.woowacourse.zzimkkong.domain.TimeSlot;
import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

import java.util.stream.Collectors;

public class InvalidStartEndTimeException extends ZzimkkongException {
    private static final String MESSAGE = "공간의 예약가능 시간을 확인해주세요.";
    private static final String MESSAGE_FORMAT = "예약 시간이 예약이 불가한 시간대에 걸쳐 있습니다." +
            System.getProperty("line.separator") +
            System.getProperty("line.separator") +
            "예약 요청 시간: %s" +
            System.getProperty("line.separator") +
            "예약 불가 시간대: %s";

    public InvalidStartEndTimeException(Settings settings, TimeSlot reservationTimeSlot) {
        super(String.format(
                MESSAGE_FORMAT,
                reservationTimeSlot,
                settings.getUnavailableTimeSlots()
                        .stream()
                        .map(TimeSlot::toString)
                        .collect(Collectors.joining(", "))),
                HttpStatus.BAD_REQUEST);
    }
}
