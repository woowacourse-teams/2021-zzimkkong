package com.woowacourse.zzimkkong;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Reservation;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static com.woowacourse.zzimkkong.CommonFixture.*;

@Component
@Profile({"local"})
public class DataLoader implements CommandLineRunner {
    private final MemberRepository members;
    private final MapRepository maps;
    private final SpaceRepository spaces;
    private final ReservationRepository reservations;

    public DataLoader(
            final MemberRepository memberRepository,
            final MapRepository mapRepository,
            final SpaceRepository spaceRepository,
            final ReservationRepository reservationRepository) {
        this.members = memberRepository;
        this.maps = mapRepository;
        this.spaces = spaceRepository;
        this.reservations = reservationRepository;
    }

    @Override
    public void run(String... args) {
        Member pobi = members.save(POBI);

        Map luther = maps.save(LUTHER);

        Space fe2 = new Space.Builder()
                .name("프론트엔드 강의실2")
                .textPosition("bottom")
                .color("#FED7D9")
                .coordinate("560, 140")
                .map(luther)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(FE_SETTING)
                .build();

        Space meetingRoom1 = new Space.Builder()
                .name("회의실1")
                .textPosition("bottom")
                .color("#FFE3AC")
                .coordinate("29, 229")
                .map(luther)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(FE_SETTING)
                .build();

        Space meetingRoom2 = new Space.Builder()
                .name("회의실2")
                .textPosition("bottom")
                .color("#FFE3AC")
                .coordinate("88, 229")
                .map(luther)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(FE_SETTING)
                .build();

        Space meetingRoom3 = new Space.Builder()
                .name("회의실3")
                .textPosition("bottom")
                .color("#FFE3AC")
                .coordinate("510, 220")
                .map(luther)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(FE_SETTING)
                .build();

        Space meetingRoom4 = new Space.Builder()
                .name("회의실4")
                .textPosition("bottom")
                .color("#FFE3AC")
                .coordinate("584, 220")
                .map(luther)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(FE_SETTING)
                .build();

        Space meetingRoom5 = new Space.Builder()
                .name("회의실5")
                .textPosition("bottom")
                .color("#FFE3AC")
                .coordinate("668, 335")
                .map(luther)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(FE_SETTING)
                .build();

        Space pairRoom1 = new Space.Builder()
                .name("페어룸1")
                .textPosition("left")
                .color("#CCDFFB")
                .coordinate("208, 289")
                .map(luther)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(FE_SETTING)
                .build();

        Space pairRoom2 = new Space.Builder()
                .name("페어룸2")
                .textPosition("left")
                .color("#CCDFFB")
                .coordinate("208, 318")
                .map(luther)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(FE_SETTING)
                .build();

        Space pairRoom3 = new Space.Builder()
                .name("페어룸3")
                .textPosition("left")
                .color("#CCDFFB")
                .coordinate("208, 347")
                .map(luther)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(FE_SETTING)
                .build();

        Space pairRoom4 = new Space.Builder()
                .name("페어룸4")
                .textPosition("left")
                .color("#CCDFFB")
                .coordinate("208, 376")
                .map(luther)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(FE_SETTING)
                .build();

        Space pairRoom5 = new Space.Builder()
                .name("페어룸5")
                .textPosition("left")
                .color("#CCDFFB")
                .coordinate("208, 404")
                .map(luther)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(FE_SETTING)
                .build();

        Space trackRoom = new Space.Builder()
                .name("트랙방")
                .textPosition("bottom")
                .color("#D8FBCC")
                .coordinate("259, 336")
                .map(luther)
                .description("시니컬하네")
                .area(SPACE_DRAWING)
                .setting(FE_SETTING)
                .build();

        List<Space> spaces = List.of(
                BE,
                FE1, fe2,
                meetingRoom1, meetingRoom2, meetingRoom3, meetingRoom4, meetingRoom5,
                pairRoom1, pairRoom2, pairRoom3, pairRoom4, pairRoom5,
                trackRoom
        );

        for (Space space : spaces) {
            this.spaces.save(space);
        }

        LocalDate targetDate = LocalDate.now().plusDays(1L);

        Reservation reservationBackEndTargetDate0To1 = new Reservation.Builder()
                .startTime(targetDate.atStartOfDay())
                .endTime(targetDate.atTime(1, 0, 0))
                .description("찜꽁 1차 회의")
                .userName("찜꽁")
                .password("1234")
                .space(BE)
                .build();

        Reservation reservationBackEndTargetDate13To14 = new Reservation.Builder()
                .startTime(targetDate.atTime(13, 0, 0))
                .endTime(targetDate.atTime(14, 0, 0))
                .description("찜꽁 2차 회의")
                .userName("찜꽁")
                .password("1234")
                .space(BE)
                .build();

        Reservation reservationBackEndTargetDate18To23 = new Reservation.Builder()
                .startTime(targetDate.atTime(18, 0, 0))
                .endTime(targetDate.atTime(23, 59, 59))
                .description("찜꽁 3차 회의")
                .userName("찜꽁")
                .password("6789")
                .space(BE)
                .build();

        Reservation reservationBackEndTheDayAfterTargetDate = new Reservation.Builder()
                .startTime(targetDate.plusDays(1L).atStartOfDay())
                .endTime(targetDate.plusDays(1L).atTime(1, 0, 0))
                .description("찜꽁 4차 회의")
                .userName("찜꽁")
                .password("1234")
                .space(BE)
                .build();

        Reservation reservationFrontEnd1TargetDate0to1 = new Reservation.Builder()
                .startTime(targetDate.atStartOfDay())
                .endTime(targetDate.atTime(1, 0, 0))
                .description("찜꽁 5차 회의")
                .userName("찜꽁")
                .password("1234")
                .space(FE1)
                .build();

        reservations.save(reservationBackEndTargetDate0To1);
        reservations.save(reservationBackEndTargetDate13To14);
        reservations.save(reservationBackEndTargetDate18To23);
        reservations.save(reservationBackEndTheDayAfterTargetDate);
        reservations.save(reservationFrontEnd1TargetDate0to1);
    }
}
