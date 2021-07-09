package com.woowacourse.zzimkkong;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.domain.Space;
import com.woowacourse.zzimkkong.repository.MapRepository;
import com.woowacourse.zzimkkong.repository.MemberRepository;
import com.woowacourse.zzimkkong.repository.SpaceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile({"local", "test"})
public class DataLoader implements CommandLineRunner {
    private final MemberRepository memberRepository;
    private final MapRepository mapRepository;
    private final SpaceRepository spaceRepository;

    public DataLoader(
            final MemberRepository memberRepository,
            final MapRepository mapRepository,
            final SpaceRepository spaceRepository) {
        this.memberRepository = memberRepository;
        this.mapRepository = mapRepository;
        this.spaceRepository = spaceRepository;
    }

    @Override
    public void run(String... args) {
        Member pobi = memberRepository.save(
                new Member("pobi@woowa.com", "test1234", "woowacourse")
        );

        Map luther = mapRepository.save(
                new Map("루터회관", pobi)
        );

        Space be = new Space("백엔드 강의실", luther);
        Space fe1 = new Space("프론트엔드 강의실1", luther);
        Space fe2 = new Space("프론트엔드 강의실2", luther);
        Space meetingRoom1 = new Space("회의실1", luther);
        Space meetingRoom2 = new Space("회의실2", luther);
        Space meetingRoom3 = new Space("회의실3", luther);
        Space meetingRoom4 = new Space("회의실4", luther);
        Space meetingRoom5 = new Space("회의실5", luther);
        Space pairRoom1 = new Space("페어룸1", luther);
        Space pairRoom2 = new Space("페어룸2", luther);
        Space pairRoom3 = new Space("페어룸3", luther);
        Space pairRoom4 = new Space("페어룸4", luther);
        Space pairRoom5 = new Space("페어룸5", luther);
        Space trackRoom = new Space("트랙방", luther);

        List<Space> spaces = List.of(
                be,
                fe1, fe2,
                meetingRoom1, meetingRoom2, meetingRoom3, meetingRoom4, meetingRoom5,
                pairRoom1, pairRoom2, pairRoom3, pairRoom4, pairRoom5,
                trackRoom
        );

        for (Space space : spaces) {
            spaceRepository.save(space);
        }
    }
}
