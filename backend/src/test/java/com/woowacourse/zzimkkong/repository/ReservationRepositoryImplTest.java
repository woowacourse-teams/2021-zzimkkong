package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.*;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.woowacourse.zzimkkong.Constants.*;
import static com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils.UTC;

class ReservationRepositoryImplTest extends RepositoryTest {
    @ParameterizedTest
    @CsvSource({"true", "false"})
    @DisplayName("멤버를 이용해 오늘 이후의 예약이 존재하는지 확인할 수 있다.")
    void existsReservationsByMember(boolean isReservationExists) {
        // given
        Member sakjung = new Member(NEW_EMAIL, PW, ORGANIZATION);
        Member savedMember = members.save(sakjung);

        Map luther = new Map(LUTHER_NAME, MAP_DRAWING_DATA, MAP_SVG, savedMember);
        maps.save(luther);

        Setting beSetting = Setting.builder()
                .availableStartTime(BE_AVAILABLE_START_TIME)
                .availableEndTime(BE_AVAILABLE_END_TIME)
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .build();

        Space be = Space.builder()
                .name(BE_NAME)
                .color(BE_COLOR)
                .description(BE_DESCRIPTION)
                .area(SPACE_DRAWING)
                .setting(beSetting)
                .map(luther)
                .build();

        spaces.save(be);

        Reservation beAmZeroOneYesterday = Reservation.builder()
                .date(LocalDate.now().minusDays(1))
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().minusDays(1).plusHours(1))
                .description(BE_AM_TEN_ELEVEN_DESCRIPTION)
                .userName(BE_AM_TEN_ELEVEN_USERNAME)
                .password(BE_AM_TEN_ELEVEN_PW)
                .space(be)
                .build();

        reservations.save(beAmZeroOneYesterday);

        if (isReservationExists) {
            Reservation beAmZeroOne = Reservation.builder()
                    .date(BE_AM_TEN_ELEVEN_START_TIME_KST.toLocalDate())
                    .startTime(BE_AM_TEN_ELEVEN_START_TIME_KST.withZoneSameInstant(UTC.toZoneId()).toLocalDateTime())
                    .endTime(BE_AM_TEN_ELEVEN_END_TIME_KST.withZoneSameInstant(UTC.toZoneId()).toLocalDateTime())
                    .description(BE_AM_TEN_ELEVEN_DESCRIPTION)
                    .userName(BE_AM_TEN_ELEVEN_USERNAME)
                    .password(BE_AM_TEN_ELEVEN_PW)
                    .space(be)
                    .build();

            reservations.save(beAmZeroOne);
        }

        // when
        Boolean hasAnyReservations = reservations.existsReservationsByMemberFromToday(savedMember);

        // then
        AssertionsForClassTypes.assertThat(hasAnyReservations).isEqualTo(isReservationExists);
    }
}
