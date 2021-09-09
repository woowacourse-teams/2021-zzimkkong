package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.exception.member.NoSuchMemberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.dao.DataAccessException;

import static com.woowacourse.zzimkkong.Constants.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MemberRepositoryTest extends RepositoryTest {
    private Member pobi;

    @BeforeEach
    void setUp() {
        pobi = new Member(EMAIL, PW, ORGANIZATION);
    }

    @Test
    @DisplayName("Member를 저장한다.")
    void save() {
        // given, when
        Member savedMember = members.save(pobi);

        // then
        assertThat(savedMember.getId()).isNotNull();
        assertThat(savedMember).isEqualTo(pobi);
    }

    @Test
    @DisplayName("저장된 멤버를 이메일을 통해 찾아올 수 있다.")
    void findByEmail() {
        // given
        Member savedMember = members.save(pobi);

        // when
        Member findMember = members.findByEmail(EMAIL)
                .orElseThrow(NoSuchMemberException::new);

        // then
        assertThat(findMember).isEqualTo(savedMember);
    }

    @Test
    @DisplayName("특정 이메일을 가진 멤버가 있는지 확인할 수 있다.")
    void existsByEmail() {
        // given
        members.save(pobi);

        // when, then
        assertThat(members.existsByEmail(EMAIL)).isTrue();
    }

    @Test
    @DisplayName("중복된 이메일을 가진 멤버를 등록하면 에러가 발생한다.")
    void duplicatedEmail() {
        // given
        members.save(pobi);

        // when
        Member sameEmailMember = new Member(EMAIL, "another123", "루터회관");

        // then
        assertThatThrownBy(() -> members.save(sameEmailMember))
                .isInstanceOf(DataAccessException.class);
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    @DisplayName("멤버의 Id를 이용해 예약이 존재하는지 확인할 수 있다.")
    void existsReservationsByMemberId(boolean isReservationExists) {
        // given
        Member savedMember = members.save(pobi);

        Map luther = new Map(LUTHER_NAME, MAP_DRAWING_DATA, MAP_IMAGE_URL, pobi);
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

        if (isReservationExists) {
            Reservation beAmZeroOne = Reservation.builder()
                    .startTime(BE_AM_TEN_ELEVEN_START_TIME)
                    .endTime(BE_AM_TEN_ELEVEN_END_TIME)
                    .description(BE_AM_TEN_ELEVEN_DESCRIPTION)
                    .userName(BE_AM_TEN_ELEVEN_USERNAME)
                    .password(BE_AM_TEN_ELEVEN_PW)
                    .space(be)
                    .build();

            reservations.save(beAmZeroOne);
        }

        // when
        Boolean hasAnyReservations = members.existsReservationsByMember(savedMember);

        // then
        assertThat(hasAnyReservations).isEqualTo(isReservationExists);
    }
}
