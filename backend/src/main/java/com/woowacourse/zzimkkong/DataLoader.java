package com.woowacourse.zzimkkong;

import com.woowacourse.zzimkkong.domain.*;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import com.woowacourse.zzimkkong.repository.ReservationRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
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
        String lectureRoomColor = "#FED7D9";
        String pairRoomColor = "#CCDFFB";
        String meetingRoomColor = "#FFE3AC";

        Member pobi = members.save(
                new Member("pobi@woowa.com",
                        "$2a$10$c3BysogWR4hnexYx60/r/e3lEUIbSs4zhW6kuX4UW733MW5/NmbW.", // test1234 입니다.
                        "woowacourse")
        );

        Map luther = maps.save(
                new Map(
                        "루터회관",
                        "{'id': '1', 'type': 'polyline', 'fill': '', 'stroke': 'rgba(111, 111, 111, 1)', 'points': '['60,250', '1,231', '242,252']', 'd': '[]', 'transform': ''}",
                        "https://d1dgzmdd5f1fx6.cloudfront.net/thumbnails/1.png",
                        pobi)
        );

        Setting defaultSetting = Setting.builder()
                .availableStartTime(LocalTime.of(9, 0))
                .availableEndTime(LocalTime.of(22, 00))
                .reservationTimeUnit(10)
                .reservationMinimumTimeUnit(10)
                .reservationMaximumTimeUnit(700)
                .reservationEnable(true)
                .enabledDayOfWeek("monday,tuesday,wednesday,thursday,friday,saturday,sunday")
                .build();

        Space be = Space.builder()
                .name("백엔드 강의실")
                .color(lectureRoomColor)
                .map(luther)
                .setting(defaultSetting)
                .build();

        Space fe1 = Space.builder()
                .name("프론트엔드 강의실1")
                .color(lectureRoomColor)
                .map(luther)
                .setting(defaultSetting)
                .build();

        Space fe2 = Space.builder()
                .name("프론트엔드 강의실2")
                .color(lectureRoomColor)
                .map(luther)
                .setting(defaultSetting)
                .build();

        Space meetingRoom1 = Space.builder()
                .name("회의실1")
                .color(meetingRoomColor)
                .map(luther)
                .setting(defaultSetting)
                .build();

        Space meetingRoom2 = Space.builder()
                .name("회의실2")
                .color(meetingRoomColor)
                .map(luther)
                .setting(defaultSetting)
                .build();

        Space meetingRoom3 = Space.builder()
                .name("회의실3")
                .color(meetingRoomColor)
                .map(luther)
                .setting(defaultSetting)
                .build();

        Space meetingRoom4 = Space.builder()
                .name("회의실4")
                .color(meetingRoomColor)
                .map(luther)
                .setting(defaultSetting)
                .build();

        Space meetingRoom5 = Space.builder()
                .name("회의실5")
                .color(meetingRoomColor)
                .map(luther)
                .setting(defaultSetting)
                .build();

        Space pairRoom1 = Space.builder()
                .name("페어룸1")
                .color(pairRoomColor)
                .map(luther)
                .setting(defaultSetting)
                .build();

        Space pairRoom2 = Space.builder()
                .name("페어룸2")
                .color(pairRoomColor)
                .map(luther)
                .setting(defaultSetting)
                .build();

        Space pairRoom3 = Space.builder()
                .name("페어룸3")
                .color(pairRoomColor)
                .map(luther)
                .setting(defaultSetting)
                .build();

        Space pairRoom4 = Space.builder()
                .name("페어룸4")
                .color(pairRoomColor)
                .map(luther)
                .setting(defaultSetting)
                .build();

        Space pairRoom5 = Space.builder()
                .name("페어룸5")
                .color(pairRoomColor)
                .map(luther)
                .setting(defaultSetting)
                .build();

        Space trackRoom = Space.builder()
                .name("트랙방")
                .color("#D8FBCC")
                .map(luther)
                .setting(defaultSetting)
                .build();

        List<Space> sampleSpaces = List.of(
                be,
                fe1, fe2,
                meetingRoom1, meetingRoom2, meetingRoom3, meetingRoom4, meetingRoom5,
                pairRoom1, pairRoom2, pairRoom3, pairRoom4, pairRoom5,
                trackRoom
        );

        for (Space space : sampleSpaces) {
            spaces.save(space);
        }

        LocalDate targetDate = LocalDate.now().plusDays(1L);

        Reservation reservationBackEndTargetDate0To1 = Reservation.builder()
                .startTime(targetDate.atStartOfDay())
                .endTime(targetDate.atTime(1, 0, 0))
                .description("찜꽁 1차 회의")
                .userName("찜꽁")
                .password("1234")
                .space(be)
                .build();

        Reservation reservationBackEndTargetDate13To14 = Reservation.builder()
                .startTime(targetDate.atTime(13, 0, 0))
                .endTime(targetDate.atTime(14, 0, 0))
                .description("찜꽁 2차 회의")
                .userName("찜꽁")
                .password("1234")
                .space(be)
                .build();

        Reservation reservationBackEndTargetDate18To23 = Reservation.builder()
                .startTime(targetDate.atTime(18, 0, 0))
                .endTime(targetDate.atTime(23, 59, 59))
                .description("찜꽁 3차 회의")
                .userName("찜꽁")
                .password("6789")
                .space(be)
                .build();

        Reservation reservationBackEndTheDayAfterTargetDate = Reservation.builder()
                .startTime(targetDate.plusDays(1L).atStartOfDay())
                .endTime(targetDate.plusDays(1L).atTime(1, 0, 0))
                .description("찜꽁 4차 회의")
                .userName("찜꽁")
                .password("1234")
                .space(be)
                .build();

        Reservation reservationFrontEnd1TargetDate0to1 = Reservation.builder()
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
