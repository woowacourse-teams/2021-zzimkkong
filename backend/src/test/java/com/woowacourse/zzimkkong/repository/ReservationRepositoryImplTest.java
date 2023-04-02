package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static com.woowacourse.zzimkkong.Constants.*;
import static com.woowacourse.zzimkkong.infrastructure.datetime.TimeZoneUtils.UTC;

class ReservationRepositoryImplTest extends RepositoryTest {
    @ParameterizedTest
    @CsvSource({"true", "false"})
    @DisplayName("멤버를 이용해 오늘 이후의 예약이 존재하는지 확인할 수 있다.")
    void existsReservationsByMember(boolean isReservationExists) {
        // given
        Member sakjung = Member.builder()
                .email(NEW_EMAIL)
                .userName(POBI)
                .emoji(ProfileEmoji.MAN_DARK_SKIN_TONE_TECHNOLOGIST)
                .password(PW)
                .organization(ORGANIZATION)
                .build();
        Member savedMember = members.save(sakjung);

        Map luther = new Map(LUTHER_NAME, MAP_DRAWING_DATA, MAP_SVG, savedMember);
        maps.save(luther);

        Setting beSetting = Setting.builder()
                .settingTimeSlot(TimeSlot.of(
                        BE_AVAILABLE_START_TIME,
                        BE_AVAILABLE_END_TIME))
                .reservationTimeUnit(BE_RESERVATION_TIME_UNIT)
                .reservationMinimumTimeUnit(BE_RESERVATION_MINIMUM_TIME_UNIT)
                .reservationMaximumTimeUnit(BE_RESERVATION_MAXIMUM_TIME_UNIT)
                .enabledDayOfWeek(BE_ENABLED_DAY_OF_WEEK)
                .priorityOrder(0)
                .build();

        Space be = Space.builder()
                .name(BE_NAME)
                .color(BE_COLOR)
                .area(SPACE_DRAWING)
                .reservationEnable(BE_RESERVATION_ENABLE)
                .spaceSettings(new Settings(List.of(beSetting)))
                .map(luther)
                .build();

        spaces.save(be);

        Reservation beAmZeroOneYesterday = Reservation.builder()
                .reservationTime(
                        ReservationTime.ofDefaultServiceZone(
                                THE_DAY_AFTER_TOMORROW.minusDays(1).atTime(0, 0),
                                THE_DAY_AFTER_TOMORROW.minusDays(1).atTime(1, 0)))
                .description(BE_AM_TEN_ELEVEN_DESCRIPTION)
                .userName(BE_AM_TEN_ELEVEN_USERNAME)
                .password(BE_AM_TEN_ELEVEN_PW)
                .space(be)
                .build();

        reservations.save(beAmZeroOneYesterday);

        if (isReservationExists) {
            Reservation beAmZeroOne = Reservation.builder()
                    .reservationTime(
                            ReservationTime.ofDefaultServiceZone(
                                    TimeZoneUtils.convertToUTC(BE_AM_TEN_ELEVEN_START_TIME_KST),
                                    TimeZoneUtils.convertToUTC(BE_AM_TEN_ELEVEN_END_TIME_KST)))
                    .description(BE_AM_TEN_ELEVEN_DESCRIPTION)
                    .userName(BE_AM_TEN_ELEVEN_USERNAME)
                    .password(BE_AM_TEN_ELEVEN_PW)
                    .space(be)
                    .build();

            reservations.save(beAmZeroOne);
        }

        // when
        Boolean hasAnyReservations = reservations.existsByMemberAndEndTimeAfter(
                savedMember,
                THE_DAY_AFTER_TOMORROW.atTime(0, 0));

        // then
        AssertionsForClassTypes.assertThat(hasAnyReservations).isEqualTo(isReservationExists);
    }
}
