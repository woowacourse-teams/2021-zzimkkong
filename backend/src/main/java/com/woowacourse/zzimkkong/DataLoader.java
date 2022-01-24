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
                        "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"950\" height=\"610\"><polyline points=\"40,560 300,560\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"300,440 130,440\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"110,440 130,440\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"110,460 110,560\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"110,350 110,440\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"110,330 300,330\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"300,270 300,560\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"220,270 220,330\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"140,270 140,330\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"240,270 300,270\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"160,270 220,270\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"110,280 110,310\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"110,310 120,310\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"120,310 120,280\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"120,280 110,280\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"110,500 40,500\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"50,560 50,200\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"40,200 100,200\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"120,200 120,130\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"120,130 40,130\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"90,200 90,240\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"90,240 50,240\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"140,200 300,200\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"120,60 120,140\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"300,330 400,330\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"500,330 600,330\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"450,350 430,370\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"430,370 440,370\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"440,370 440,410\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"440,410 460,410\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"460,410 460,370\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"460,370 470,370\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"470,370 450,350\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"450,260 460,260\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"460,260 460,70\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"460,70 450,70\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"450,70 450,260\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"500,260 600,260\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"500,190 520,170\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"520,170 520,120\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"520,120 540,100\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"540,100 560,100\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"560,100 580,120\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"580,120 580,260\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"600,260 630,260\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"600,220 630,220\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"600,180 630,180\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"600,140 630,140\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"600,100 630,100\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"630,140 630,160\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"630,180 630,200\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"630,220 630,240\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"600,330 600,560\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"630,260 630,290\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"630,290 500,290\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"500,290 500,260\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"300,270 330,270\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"330,270 330,330\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"680,230 680,290\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"680,230 680,180\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"300,60 300,200\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"40,60 910,60\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"910,60 910,560\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"910,560 600,560\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"680,230 910,230\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"820,230 820,290\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"730,230 730,290\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"680,290 800,290\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"840,290 910,290\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"680,60 680,100\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"40,60 40,560\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"500,190 500,60\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"370,330 370,560\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"300,560 370,560\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"530,330 530,560\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"530,560 600,560\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"600,60 600,260\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><polyline points=\"630,80 630,120\" stroke=\"#333333\" stroke-width=\"3\" stroke-linecap=\"round\"></polyline><g data-testid=\"42\" class=\"sc-ciSkZP bwSYJA\"><rect x=\"110\" y=\"440\" width=\"190\" height=\"120\" fill=\"#0060FF\" opacity=\"0.3\" class=\"sc-jcwpoC kyvWZW\"></rect><text x=\"205\" y=\"500\" class=\"sc-carFqZ hTwlVf\">프론트 강의실1</text></g><g data-testid=\"43\" class=\"sc-ciSkZP bwSYJA\"><rect x=\"110\" y=\"330\" width=\"190\" height=\"110\" fill=\"#0060FF\" opacity=\"0.3\" class=\"sc-jcwpoC kyvWZW\"></rect><text x=\"205\" y=\"385\" class=\"sc-carFqZ hTwlVf\">프론트 강의실 2</text></g><g data-testid=\"46\" class=\"sc-ciSkZP bwSYJA\"><rect x=\"220\" y=\"270\" width=\"80\" height=\"60\" fill=\"#EB3933\" opacity=\"0.3\" class=\"sc-jcwpoC kyvWZW\"></rect><text x=\"260\" y=\"300\" class=\"sc-carFqZ hTwlVf\">회의실 3</text></g><g data-testid=\"47\" class=\"sc-ciSkZP bwSYJA\"><rect x=\"140\" y=\"270\" width=\"80\" height=\"60\" fill=\"#EB3933\" opacity=\"0.3\" class=\"sc-jcwpoC kyvWZW\"></rect><text x=\"180\" y=\"300\" class=\"sc-carFqZ hTwlVf\">회의실 4</text></g><g data-testid=\"48\" class=\"sc-ciSkZP bwSYJA\"><rect x=\"40\" y=\"130\" width=\"80\" height=\"70\" fill=\"#EB3933\" opacity=\"0.3\" class=\"sc-jcwpoC kyvWZW\"></rect><text x=\"80\" y=\"165\" class=\"sc-carFqZ hTwlVf\">회의실 5</text></g><g data-testid=\"50\" class=\"sc-ciSkZP bwSYJA\"><rect x=\"600\" y=\"220\" width=\"30\" height=\"40\" fill=\"#8B5CF6\" opacity=\"0.3\" class=\"sc-jcwpoC kyvWZW\"></rect><text x=\"615\" y=\"240\" class=\"sc-carFqZ hTwlVf\">1</text></g><g data-testid=\"51\" class=\"sc-ciSkZP bwSYJA\"><rect x=\"600\" y=\"180\" width=\"30\" height=\"40\" fill=\"#8B5CF6\" opacity=\"0.3\" class=\"sc-jcwpoC kyvWZW\"></rect><text x=\"615\" y=\"200\" class=\"sc-carFqZ hTwlVf\">2</text></g><g data-testid=\"52\" class=\"sc-ciSkZP bwSYJA\"><rect x=\"600\" y=\"140\" width=\"30\" height=\"40\" fill=\"#8B5CF6\" opacity=\"0.3\" class=\"sc-jcwpoC kyvWZW\"></rect><text x=\"615\" y=\"160\" class=\"sc-carFqZ hTwlVf\">3</text></g><g data-testid=\"53\" class=\"sc-ciSkZP bwSYJA\"><rect x=\"600\" y=\"100\" width=\"30\" height=\"40\" fill=\"#8B5CF6\" opacity=\"0.3\" class=\"sc-jcwpoC kyvWZW\"></rect><text x=\"615\" y=\"120\" class=\"sc-carFqZ hTwlVf\">4</text></g><g data-testid=\"55\" class=\"sc-ciSkZP bwSYJA\"><rect x=\"40\" y=\"500\" width=\"70\" height=\"60\" fill=\"#FFEE58\" opacity=\"0.3\" class=\"sc-jcwpoC kyvWZW\"></rect><text x=\"75\" y=\"530\" class=\"sc-carFqZ hTwlVf\">방송실</text></g><g data-testid=\"56\" class=\"sc-ciSkZP bwSYJA\"><rect x=\"50\" y=\"200\" width=\"40\" height=\"40\" fill=\"#FFEE58\" opacity=\"0.3\" class=\"sc-jcwpoC kyvWZW\"></rect><text x=\"70\" y=\"220\" class=\"sc-carFqZ hTwlVf\">전화</text></g><g data-testid=\"67\" class=\"sc-ciSkZP bwSYJA\"><rect x=\"600\" y=\"330\" width=\"310\" height=\"230\" fill=\"#6BC2F9\" opacity=\"0.3\" class=\"sc-jcwpoC kyvWZW\"></rect><text x=\"755\" y=\"445\" class=\"sc-carFqZ hTwlVf\">백엔드 강의실</text></g><g data-testid=\"68\" class=\"sc-ciSkZP bwSYJA\"><rect x=\"820\" y=\"230\" width=\"90\" height=\"60\" fill=\"#EB3933\" opacity=\"0.3\" class=\"sc-jcwpoC kyvWZW\"></rect><text x=\"865\" y=\"260\" class=\"sc-carFqZ hTwlVf\">회의실 1</text></g><g data-testid=\"69\" class=\"sc-ciSkZP bwSYJA\"><rect x=\"730\" y=\"230\" width=\"90\" height=\"60\" fill=\"#EB3933\" opacity=\"0.3\" class=\"sc-jcwpoC kyvWZW\"></rect><text x=\"775\" y=\"260\" class=\"sc-carFqZ hTwlVf\">회의실 2</text></g><g data-testid=\"70\" class=\"sc-ciSkZP bwSYJA\"><rect x=\"600\" y=\"60\" width=\"30\" height=\"40\" fill=\"#8B5CF6\" opacity=\"0.3\" class=\"sc-jcwpoC kyvWZW\"></rect><text x=\"615\" y=\"80\" class=\"sc-carFqZ hTwlVf\">5</text></g><g data-testid=\"71\" class=\"sc-ciSkZP bwSYJA\"><rect x=\"500\" y=\"60\" width=\"100\" height=\"200\" fill=\"#5CAC1E\" opacity=\"0.3\" class=\"sc-jcwpoC kyvWZW\"></rect><text x=\"550\" y=\"160\" class=\"sc-carFqZ hTwlVf\">트랙방</text></g></svg>",
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
                .date(targetDate)
                .startTime(targetDate.atStartOfDay())
                .endTime(targetDate.atTime(1, 0, 0))
                .date(targetDate)
                .description("찜꽁 1차 회의")
                .userName("찜꽁")
                .password("1234")
                .space(be)
                .build();

        Reservation reservationBackEndTargetDate13To14 = Reservation.builder()
                .date(targetDate)
                .startTime(targetDate.atTime(13, 0, 0))
                .endTime(targetDate.atTime(14, 0, 0))
                .date(targetDate)
                .description("찜꽁 2차 회의")
                .userName("찜꽁")
                .password("1234")
                .space(be)
                .build();

        Reservation reservationBackEndTargetDate18To23 = Reservation.builder()
                .date(targetDate)
                .startTime(targetDate.atTime(18, 0, 0))
                .endTime(targetDate.atTime(23, 59, 59))
                .date(targetDate)
                .description("찜꽁 3차 회의")
                .userName("찜꽁")
                .password("6789")
                .space(be)
                .build();

        Reservation reservationBackEndTheDayAfterTargetDate = Reservation.builder()
                .date(targetDate)
                .startTime(targetDate.plusDays(1L).atStartOfDay())
                .endTime(targetDate.plusDays(1L).atTime(1, 0, 0))
                .date(targetDate)
                .description("찜꽁 4차 회의")
                .userName("찜꽁")
                .password("1234")
                .space(be)
                .build();

        Reservation reservationFrontEnd1TargetDate0to1 = Reservation.builder()
                .date(targetDate)
                .startTime(targetDate.atStartOfDay())
                .endTime(targetDate.atTime(1, 0, 0))
                .date(targetDate)
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
