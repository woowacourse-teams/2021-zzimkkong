package com.woowacourse.zzimkkong.infrastructure;

import java.time.LocalDateTime;

public interface TimeValidator {
    void validateStartTimeInPast(final LocalDateTime startDateTime);
}
