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
        Member pobi = members.save(
                new Member("pobi@woowa.com", "test1234", "woowacourse")
        );

        Map luther = maps.save(
                new Map("루터회관", pobi)
        );

        Space be = new Space.Builder()
                .name("백엔드 강의실")
                .color("bottom")
                .color("#FED7D9")
                .coordinate("100, 90")
                .map(luther)
                .build();

        Space fe1 = new Space.Builder()
                .name("프론트엔드 강의실1")
                .color("bottom")
                .color("#FED7D9")
                .coordinate("560, 40")
                .map(luther)
                .build();

        Space fe2 = new Space.Builder()
                .name("프론트엔드 강의실2")
                .color("bottom")
                .color("#FED7D9")
                .coordinate("560, 140")
                .map(luther)
                .build();

        Space meetingRoom1 = new Space.Builder()
                .name("회의실1")
                .color("bottom")
                .color("#FFE3AC")
                .coordinate("29, 229")
                .map(luther)
                .build();

        Space meetingRoom2 = new Space.Builder()
                .name("회의실2")
                .color("bottom")
                .color("#FFE3AC")
                .coordinate("88, 229")
                .map(luther)
                .build();

        Space meetingRoom3 = new Space.Builder()
                .name("회의실3")
                .color("bottom")
                .color("#FFE3AC")
                .coordinate("510, 220")
                .map(luther)
                .build();

        Space meetingRoom4 = new Space.Builder()
                .name("회의실4")
                .color("bottom")
                .color("#FFE3AC")
                .coordinate("584, 220")
                .map(luther)
                .build();

        Space meetingRoom5 = new Space.Builder()
                .name("회의실5")
                .color("bottom")
                .color("#FFE3AC")
                .coordinate("668, 335")
                .map(luther)
                .build();

        Space pairRoom1 = new Space.Builder()
                .name("페어룸1")
                .color("left")
                .color("#CCDFFB")
                .coordinate("208, 289")
                .map(luther)
                .build();

        Space pairRoom2 = new Space.Builder()
                .name("페어룸2")
                .color("left")
                .color("#CCDFFB")
                .coordinate("208, 318")
                .map(luther)
                .build();

        Space pairRoom3 = new Space.Builder()
                .name("페어룸3")
                .color("left")
                .color("#CCDFFB")
                .coordinate("208, 347")
                .map(luther)
                .build();

        Space pairRoom4 = new Space.Builder()
                .name("페어룸4")
                .color("left")
                .color("#CCDFFB")
                .coordinate("208, 376")
                .map(luther)
                .build();

        Space pairRoom5 = new Space.Builder()
                .name("페어룸5")
                .color("left")
                .color("#CCDFFB")
                .coordinate("208, 404")
                .map(luther)
                .build();

        Space trackRoom = new Space.Builder()
                .name("트랙방")
                .color("bottom")
                .color("#D8FBCC")
                .coordinate("259, 336")
                .map(luther)
                .build();

        List<Space> spaces = List.of(
                be,
                fe1, fe2,
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
                .space(be)
                .build();

        Reservation reservationBackEndTargetDate13To14 = new Reservation.Builder()
                .startTime(targetDate.atTime(13, 0, 0))
                .endTime(targetDate.atTime(14, 0, 0))
                .description("찜꽁 2차 회의")
                .userName("찜꽁")
                .password("1234")
                .space(be)
                .build();

        Reservation reservationBackEndTargetDate18To23 = new Reservation.Builder()
                .startTime(targetDate.atTime(18, 0, 0))
                .endTime(targetDate.atTime(23, 59, 59))
                .description("찜꽁 3차 회의")
                .userName("찜꽁")
                .password("6789")
                .space(be)
                .build();

        Reservation reservationBackEndTheDayAfterTargetDate = new Reservation.Builder()
                .startTime(targetDate.plusDays(1L).atStartOfDay())
                .endTime(targetDate.plusDays(1L).atTime(1, 0, 0))
                .description("찜꽁 4차 회의")
                .userName("찜꽁")
                .password("1234")
                .space(be)
                .build();

        Reservation reservationFrontEnd1TargetDate0to1 = new Reservation.Builder()
                .startTime(targetDate.atStartOfDay())
                .endTime(targetDate.atTime(1, 0, 0))
                .description("찜꽁 5차 회의")
                .userName("찜꽁")
                .password("1234")
                .space(fe1)
                .build();

        reservations.save(reservationBackEndTargetDate0To1);
        reservations.save(reservationBackEndTargetDate13To14);
        reservations.save(reservationBackEndTargetDate18To23);
        reservations.save(reservationBackEndTheDayAfterTargetDate);
        reservations.save(reservationFrontEnd1TargetDate0to1);
    }
}
