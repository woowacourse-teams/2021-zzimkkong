package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Map;
import com.woowacourse.zzimkkong.domain.Member;
import com.woowacourse.zzimkkong.exception.NoSuchMapException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class MapRepositoryTest extends RepositoryTest {
    public static final Member MEMBER = new Member("Sally@gmail.com", "1234", "Sally");
    public static final Map MAP = new Map("롯데몰", MEMBER);

    @Autowired
    private MapRepository mapRepository;

    @Test
    @DisplayName("id로부터 저장된 Map을 찾아올 수 있다.")
    void findById() {
        //given
        Map savedMap = mapRepository.save(MAP);

        //when
        Map findMap = mapRepository.findById(savedMap.getId())
                .orElseThrow(NoSuchMapException::new);

        //then
        assertThat(savedMap).isEqualTo(findMap);
    }
}
