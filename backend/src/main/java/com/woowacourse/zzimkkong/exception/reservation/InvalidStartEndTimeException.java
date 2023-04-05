package com.woowacourse.zzimkkong.exception.reservation;

import com.woowacourse.zzimkkong.domain.TimeSlot;
import com.woowacourse.zzimkkong.exception.ZzimkkongException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static com.woowacourse.zzimkkong.infrastructure.message.MessageUtils.LINE_SEPARATOR;

public class InvalidStartEndTimeException extends ZzimkkongException {
    private static final String MESSAGE_FORMAT = "예약 시간이 예약이 불가한 시간대에 걸쳐 있습니다." +
            LINE_SEPARATOR +
            LINE_SEPARATOR +
            "예약 요청 시간: %s" +
            LINE_SEPARATOR +
            "예약 불가 시간대: %s";

    public InvalidStartEndTimeException(
            final List<TimeSlot> unavailableTimeSlots,
            final TimeSlot reservationTimeSlot) {
        super(String.format(
                        MESSAGE_FORMAT,
                        reservationTimeSlot,
                        unavailableTimeSlots
                                .stream()
                                .map(TimeSlot::toString)
                                .collect(Collectors.joining(", "))),
                HttpStatus.BAD_REQUEST);
    }
}
