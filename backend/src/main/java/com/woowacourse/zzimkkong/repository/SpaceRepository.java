package com.woowacourse.zzimkkong.repository;

import com.woowacourse.zzimkkong.domain.Space;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpaceRepository extends JpaRepository<Space, Long> {
}
